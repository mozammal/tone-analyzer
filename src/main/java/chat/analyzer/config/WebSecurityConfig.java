package chat.analyzer.config;

import chat.analyzer.auth.service.CustomAuthenticationProvider;
import chat.analyzer.auth.service.UserServiceImpl;
import chat.analyzer.dao.UserAccountDao;
import chat.analyzer.domain.entity.UserAccount;
import chat.analyzer.domain.repository.UserAccountRepository;
import chat.analyzer.service.token.TokenService;
import chat.analyzer.utility.CommonUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.*;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import org.springframework.web.filter.CompositeFilter;
import reactor.core.support.UUIDUtils;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

/** Created by mozammal on 4/18/17. */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableOAuth2Client
@EnableAuthorizationServer
@Order(6)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
    implements AuthorizationServerConfigurer {

  public static final String LIVE_CHAT_URI = "/chat";

  public static final String LOGIN_GOOGLE_URI = "/login/google";

  public static final String DISPLAY_NAME = "displayName";

  public static final String LOGIN_URI = "/login";

  public static final String LOGOUT_URI = "/logout";

  public static final String ADMIN_URI = "/admin";

  public static final String RESOURCES_URI = "/resources/static/**";

  public static final String REGISTRATION_URI = "/userRegistration/**";

  public static final String ADMIN_ROLE_NAME = "ADMIN";

  public static final String USER_ROLE_NAME = "USER";

  public static final String ROLE_ADMIN = "ROLE_ADMIN";

  public static final String ROLE_USER = "ROLE_USER";

  public static final String NAME = "name";

  public static final String PASSWORD = "password";

  public static final String ACTUATOR_ROLE_NAME = "ACTUATOR";

  public static final String ADMIN_PANEL_URI = "/admin-login/**";

  public static final String REMEMBER_ME = "remember-me";

  public static final String CONFIRMATION_EMAIL = "/confirmationEmail/**";

  public static final String CHAT_ANONYMOUS = "/chat/anonymous/**";

  public static final String LOGIN = "/login/**";

  public static final String ADMIN = "/admin/**";

  public static final String HEALTH = "/health/**";

  public static final String METRICS = "/metrics/**";

  public static final String INFO = "/info/**";

  public static final String ACTUATOR = "/actuator/**";

  public static final String CHAT = "/chat/**";

  public static final String RESOURCES = "/resources/**";

  public static final String STATIC = "/static/**";

  public static final String CSS = "/css/**";

  public static final String JS = "/js/**";

  public static final String IMAGES = "/images/**";

  @Autowired private UserDetailsService userDetailsService;

  private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

  private final OAuth2ClientContext oauth2ClientContext;

  private final ClientDetailsService clientDetailsService;

  @Autowired private UserAccountRepository userRepository;

  @Autowired private UserServiceImpl userService;

  @Autowired private CustomAuthenticationProvider authProvider;

  @Autowired TokenService persistentTokenRepository;

  @Autowired private CommonUtility commonUtility;

  @Autowired private UserAccountDao userAccountDao;

  @Autowired
  public WebSecurityConfig(
      OAuth2ClientContext oauth2ClientContext, ClientDetailsService clientDetailsService) {
    super();
    this.oauth2ClientContext = oauth2ClientContext;
    this.clientDetailsService = clientDetailsService;
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  public void configure(
      AuthorizationServerEndpointsConfigurer authorizationServerEndpointsConfigurer)
      throws Exception {}

  @Configuration
  @EnableResourceServer
  protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
      http.antMatcher("/chat").authorizeRequests().anyRequest().authenticated();
    }
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.headers().frameOptions().sameOrigin();
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    http.authorizeRequests()
        .antMatchers(
            LOGIN,
            RESOURCES_URI,
            REGISTRATION_URI,
            ADMIN_PANEL_URI,
            CHAT_ANONYMOUS,
            CONFIRMATION_EMAIL)
        .permitAll()
        .antMatchers(ADMIN, HEALTH, METRICS, INFO, ACTUATOR)
        .hasRole(ADMIN_ROLE_NAME)
        .antMatchers(CHAT)
        .hasAnyRole(USER_ROLE_NAME, ADMIN_ROLE_NAME, ACTUATOR_ROLE_NAME)
        .anyRequest()
        .authenticated()
        .and()
        .csrf()
        .disable()
        .formLogin()
        .loginPage(LOGIN_URI)
        .defaultSuccessUrl(LIVE_CHAT_URI)
        .successHandler(
            new AuthenticationSuccessHandler() {
              @Override
              public void onAuthenticationSuccess(
                  HttpServletRequest request,
                  HttpServletResponse response,
                  Authentication authentication)
                  throws IOException, ServletException {

                Collection<? extends GrantedAuthority> authorities =
                    authentication.getAuthorities();
                boolean isAdmin = authorities.contains(new SimpleGrantedAuthority(ROLE_ADMIN));
                boolean isUser = authorities.contains(new SimpleGrantedAuthority(ROLE_USER));
                if (isAdmin) {
                  redirectStrategy.sendRedirect(request, response, ADMIN_URI);
                } else if (isUser) {
                  redirectStrategy.sendRedirect(request, response, LIVE_CHAT_URI);
                }
              }
            })
        .failureHandler(
            new AuthenticationFailureHandler() {
              @Override
              public void onAuthenticationFailure(
                  HttpServletRequest httpServletRequest,
                  HttpServletResponse httpServletResponse,
                  AuthenticationException e)
                  throws IOException, ServletException {
                redirectStrategy.sendRedirect(
                    httpServletRequest, httpServletResponse, LOGIN_URI + "?error");
              }
            })
        .permitAll()
        .usernameParameter(NAME)
        .passwordParameter(PASSWORD)
        .and()
        .rememberMe()
        .alwaysRemember(true)
        .rememberMeParameter(REMEMBER_ME)
        .rememberMeCookieName(REMEMBER_ME)
        .userDetailsService(userDetailsService)
        .tokenRepository(persistentTokenRepository)
        .tokenValiditySeconds(864000)
        .and()
        .logout()
        .logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_URI))
        .deleteCookies(REMEMBER_ME)
        .logoutSuccessUrl(LOGIN_URI)
        .and()
        .csrf()
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .and()
        .authorizeRequests()
        .anyRequest()
        .authenticated()
        .and()
        .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(RESOURCES, STATIC, CSS, JS, IMAGES);
  }

  @Bean
  FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
    FilterRegistrationBean registration = new FilterRegistrationBean();
    registration.setFilter(filter);
    registration.setOrder(-100);
    return registration;
  }

  @Bean
  @ConfigurationProperties("google")
  ClientResources google() {
    return new ClientResources();
  }

  private Filter ssoFilter() {
    CompositeFilter filter = new CompositeFilter();
    List<Filter> filters = new ArrayList<>();
    filters.add(ssoFilter(google(), LOGIN_GOOGLE_URI));
    filter.setFilters(filters);
    return filter;
  }

  private Filter ssoFilter(ClientResources client, String path) {

    OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationFilter =
        new OAuth2ClientAuthenticationProcessingFilter(path);
    OAuth2RestTemplate oAuth2RestTemplate =
        new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
    oAuth2ClientAuthenticationFilter.setRestTemplate(oAuth2RestTemplate);
    UserInfoTokenServices tokenServices =
        new UserInfoTokenServices(
            client.getResource().getUserInfoUri(), client.getClient().getClientId());
    tokenServices.setRestTemplate(oAuth2RestTemplate);
    oAuth2ClientAuthenticationFilter.setTokenServices(tokenServices);
    oAuth2ClientAuthenticationFilter.setAuthenticationSuccessHandler(
        new AuthenticationSuccessHandler() {
          private final Logger LOG = LoggerFactory.getLogger(AuthenticationSuccessHandler.class);

          @Override
          public void onAuthenticationSuccess(
              HttpServletRequest httpServletRequest,
              HttpServletResponse httpServletResponse,
              Authentication authentication)
              throws IOException, ServletException {

            String principalNameFromAuthentication =
                commonUtility.findPrincipalNameFromAuthentication(authentication);

            if (authentication.isAuthenticated()) {
              UserAccount userAccount = userAccountDao.findByName(principalNameFromAuthentication);

              if (userAccount == null) {
                userService.save(
                    new UserAccount(
                        principalNameFromAuthentication, UUIDUtils.random().toString()));
              }
              redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, LIVE_CHAT_URI);
            }
          }
        });
    oAuth2ClientAuthenticationFilter.setAuthenticationFailureHandler(
        new AuthenticationFailureHandler() {
          @Override
          public void onAuthenticationFailure(
              HttpServletRequest httpServletRequest,
              HttpServletResponse httpServletResponse,
              AuthenticationException e)
              throws IOException, ServletException {
            redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, LOGIN_URI);
          }
        });
    return oAuth2ClientAuthenticationFilter;
  }

  @Override
  public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {}

  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients.withClientDetails(this.clientDetailsService);
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authProvider);
    auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
  }
}

class ClientResources {

  @NestedConfigurationProperty
  private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

  @NestedConfigurationProperty
  private ResourceServerProperties resource = new ResourceServerProperties();

  public AuthorizationCodeResourceDetails getClient() {
    return client;
  }

  public ResourceServerProperties getResource() {
    return resource;
  }
}
