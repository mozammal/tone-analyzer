package tone.analyzer.web;

import java.io.IOException;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tone.analyzer.auth.service.*;
import tone.analyzer.capcha.service.ICaptchaService;
import tone.analyzer.dao.UserAccountDao;
import tone.analyzer.domain.entity.Account;
import tone.analyzer.domain.entity.EmailInvitation;
import tone.analyzer.domain.repository.AccountRepository;
import tone.analyzer.service.admin.AdminService;
import tone.analyzer.utility.ToneAnalyzerUtility;
import tone.analyzer.validator.AccountValidator;
import tone.analyzer.validator.EmailInvitationValidator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by mozammal on 4/18/17.
 */
@Controller
public class UserDetailsWebController {


    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsWebController.class);

    public static final String ERROR_ATTRIBUTED = "error";

    public static final String ERROR_MESSAGE_UNSUCCESSFUL_LOGIN =
            "Your username and password is invalid.";

    public static final String MESSAGE_ATTRIBUTED = "message";

    public static final String LOGGED_OUT_SUCCESSFUL_MESSAGE =
            "You have been logged out successfully.";

    public static final String ADMIN_LOGIN_VIEW = "adminLogin";

    public static final String USERS_REGISTRATION_VIEW = "userRegistration";

    public static final String LOGIN_VIEW = "login";

    public static final String CHAT_VIEW = "chat";

    public static final String ADMIN_PANEL_VIEW = "adminPanel";

    public static final String USER_NAME = "userName";

    public static final String USER_LIST = "userList";

    public static final String USER_REGISTRATION_URI = "/userRegistration";

    public static final String LIVE_CHAT_URI = "/chat";

    public static final String ROOT_URI = "/";

    public static final String ACCOUNT_FORM = "accountForm";

    public static final String USER_REGISTRATION_EMAIL = "userRegistrationEmail";

    @Autowired
    @Qualifier("securityServiceImpl")
    private SecurityService securityServiceImpl;

    @Autowired
    @Qualifier("anonymousSecurityServiceImpl")
    private SecurityService anonymousSecurityServiceImpl;

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userServiceImpl;

    @Autowired
    @Qualifier("anonymousUserServiceImpl")
    private UserService anonymousUserServiceImpl;

    @Autowired
    private AccountValidator accountValidator;

    @Autowired
    private EmailInvitationValidator emailInvitationValidator;

    @Autowired
    private AdminService adminService;

    @Autowired
    private IEmailInvitationService emailInvitationService;

    @Autowired
    private UserAccountDao userAccountDao;

    @Autowired
    private AccountRepository userAccountRepository;

    @Autowired
    private ToneAnalyzerUtility toneAnalyzerUtility;

    @Autowired
    private ICaptchaService captchaService;

    @Autowired
    private ServletContext servletContext;

    @RequestMapping(value = "/adminLogin", method = RequestMethod.GET)
    public String adminPanel(Model model) {

        model.addAttribute(ACCOUNT_FORM, new Account());

        return ADMIN_LOGIN_VIEW;
    }

    @RequestMapping(value = USER_REGISTRATION_URI, method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute(ACCOUNT_FORM, new Account());

        return USERS_REGISTRATION_VIEW;
    }

    @RequestMapping(value = USER_REGISTRATION_URI, method = RequestMethod.POST)
    public String registration(
            @ModelAttribute("accountForm") Account accountForm,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes)
            throws Exception {

        accountValidator.validate(accountForm, bindingResult);
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            return USERS_REGISTRATION_VIEW;
        }

   /* String googleReCapcha = request.getParameter("g-recaptcha-response");
    captchaService.processResponse(googleReCapcha);*/

        String plainTextPassword = accountForm.getPassword();
        userServiceImpl.save(accountForm);
        securityServiceImpl.autoLogin(accountForm.getName(), plainTextPassword, request, response);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account user = userServiceImpl.findByName(auth.getName());
        redirectAttributes.addFlashAttribute(USER_NAME, user.getName());

        return "redirect:/chat";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {

        if (error != null) {
            model.addAttribute(ERROR_ATTRIBUTED, ERROR_MESSAGE_UNSUCCESSFUL_LOGIN);
        }
        if (logout != null) {
            model.addAttribute(MESSAGE_ATTRIBUTED, LOGGED_OUT_SUCCESSFUL_MESSAGE);
        }
        return LOGIN_VIEW;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(
            value = {ROOT_URI, LIVE_CHAT_URI},
            method = RequestMethod.GET
    )
    public String chat(Model model, HttpServletResponse response) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUserName = toneAnalyzerUtility.findPrincipalNameFromAuthentication(auth);
        Account loggedInUser = userAccountRepository.findByName(loggedInUserName);

        model.addAttribute("username", loggedInUserName);
        String fileLocation =
                loggedInUser.getDocumentMetaData() != null
                        ? loggedInUser.getDocumentMetaData().getThumbNail()
                        : null;
        model.addAttribute("userAvatar", fileLocation);

        return CHAT_VIEW;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String admin(Model model) {

        List<Account> userList = adminService.fetchAllUsers();
        model.addAttribute(USER_LIST, userList);
        return ADMIN_PANEL_VIEW;
    }

    @RequestMapping(value = "/confirmationEmail", method = RequestMethod.GET)
    public String confirmationUserByEmail(
            Model model,
            @RequestParam("token") String token,
            @RequestParam("sender") String sender,
            @RequestParam("receiver") String receiver) {

        if (token == null ||
                StringUtils.isBlank((String) token)) {
            return "redirect:/login";
        }

        EmailInvitation emailInvitationServiceByToekn = emailInvitationService
                .findByToeknAndSenderAndReceiver(token, sender, receiver);

        if (emailInvitationServiceByToekn == null) {
            return "redirect:/login";
        }

        model.addAttribute("confirmationToken", emailInvitationServiceByToekn.getToken());
        model.addAttribute("invitedBy", emailInvitationServiceByToekn.getSender());
        model.addAttribute("accountFromRegistrationByEmail", new Account());
        return "userRegistrationEmail";
    }


    @RequestMapping(value = "/errorConfirmationEmail", method = RequestMethod.GET)
    public String confirmationUserByEmail(
            Model model,
            @RequestParam("token") String token) {

        EmailInvitation emailInvitationServiceByToekn = emailInvitationService
                .findByToken(token);

        if (emailInvitationServiceByToekn == null) {
            return "redirect:/login";
        }

        model.addAttribute("confirmationToken", emailInvitationServiceByToekn.getToken());
        model.addAttribute("invitedBy", emailInvitationServiceByToekn.getSender());
        model.addAttribute("accountFromRegistrationByEmail", new Account());
        return "userRegistrationEmail";
    }


    @RequestMapping(value = "/confirmationEmail", method = RequestMethod.POST)
    public String processConfirmationForm(
            @ModelAttribute("accountFromRegistrationByEmail") Account accountFromRegistrationByEmail,
            BindingResult bindingResult,
            @RequestParam Map requestParams,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redir)
            throws UnsupportedEncodingException {

        emailInvitationValidator.validate(accountFromRegistrationByEmail, bindingResult);
        if (bindingResult.hasErrors()) {
            LOG.info("error bind error inside method processConfirmationForm: ");
            return "redirect:/login";
        }
        EmailInvitation token = emailInvitationService.findByToken((String) requestParams.get("token"));

        if (token == null) {
            bindingResult.reject("password");
            redir.addFlashAttribute("errorMessage", "Your token has expired or invalid.");
            return "redirect:/login";
        }

        Object userPassword = requestParams.get("password");
        Object userName = requestParams.get("name");

        if (userPassword == null ||
                StringUtils.isBlank((String) userPassword) || userName == null
                || StringUtils.isBlank((String) userName)) {
            return "redirect:/login";
        }

        String password = (String) userPassword;
        String name = (String) userName;
        Account account = new Account(name.trim(), password.trim());
        userAccountDao
                .processEmailInvitationAndUpdateBuddyListIfAbsent(token, account, userServiceImpl);

        securityServiceImpl.autoLogin(account.getName(), password, request, response);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account user = userServiceImpl.findByName(auth.getName());
        redir.addFlashAttribute(USER_NAME, user.getName());
        return "redirect:/chat?invited=" + URLEncoder.encode(token.getSender(), "UTF-8");
    }

    @RequestMapping(value = "/chat/anonymous", method = RequestMethod.GET)
    public String anonymousLoginForChat(Model model, @RequestParam("token") String token) {

        if (token == null ||
                StringUtils.isBlank((String) token)) {
            return "redirect:/login";
        }

        EmailInvitation anonymousInvitationToken = emailInvitationService
                .findByToken(token);

        if (anonymousInvitationToken == null) {
            return "redirect:/login";
        }
        model.addAttribute("confirmationToken", anonymousInvitationToken.getToken());
        model.addAttribute("invitedBy", anonymousInvitationToken.getSender());
        return "anonymousUserRegistration";
    }

    @RequestMapping(value = "/chat/anonymous", method = RequestMethod.POST)
    public String processInvitationForAnonymousUsers(
            @ModelAttribute("accountFromRegistrationByEmail") Account accountFromRegistrationByEmail,
            BindingResult bindingResult,
            @RequestParam Map requestParams,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redir)
            throws UnsupportedEncodingException {

        Boolean alreadyLoggedIn = false;
        String name = null;
        Object userName = requestParams.get("name");

        if (userName == null || StringUtils.isBlank((String) userName)) {
            return "redirect:/login";
        }

        EmailInvitation emailToken = emailInvitationService
                .findByToken((String) requestParams.get("token"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Account account = userAccountRepository.findByName((String) userName);
        if (account == null) {
            account = new Account(((String) userName).trim(), UUID.randomUUID().toString());
        }

        userAccountDao.processEmailInvitationAndUpdateBuddyListIfAbsent(emailToken, account,
                anonymousUserServiceImpl);
        anonymousSecurityServiceImpl
                .autoLogin(account.getName(), account.getPassword(), request, response);
        redir.addFlashAttribute(USER_NAME, userName);
        return "redirect:/chat?invited=" + URLEncoder.encode(emailToken.getSender(), "UTF-8");
    }
}