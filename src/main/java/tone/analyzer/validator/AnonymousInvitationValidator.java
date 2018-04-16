package tone.analyzer.validator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import tone.analyzer.auth.service.UserService;
import tone.analyzer.auth.service.UserServiceImpl;
import tone.analyzer.domain.entity.Account;

import java.util.regex.Pattern;

/**
 * Created by user on 3/13/2018.
 */

@Component
public class AnonymousInvitationValidator implements Validator {

    @Value("${user.name.minimum.length}")
    private Integer USER_NAME_MINIMUM_LENGTH;

    @Value("${user.name.maximum.length}")
    private Integer USER_NAME_MAXIMUM_LENGTH;

    @Value("${user.password.minimum.length}")
    private Integer USER_PASSWORD_MINIMUM_LENGTH;

    @Value("${user.password.maximum.length}")
    private Integer USER_PASSWORD_MAXIMUM_LENGTH;

    public static final String NAME = "name";

    public static final String SIZE_OF_NAME = "Size.AccountForm.name";

    public static final String DUPLICATE_NAME = "Duplicate.AccountForm.name";

    public static final String EMPTY_NAME = "Null.AccountForm.name";

    public static final String PASSWORD = "password";

    public static final String NOT_EMPTY_MESSAGE = "NotEmpty.AccountForm";

    public static final String PASSWORD_SIZE_MESSAGE = "Size.AccountForm.password";

    @Autowired
    private UserServiceImpl userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return Account.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Account account = (Account) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, NAME, NOT_EMPTY_MESSAGE);
        String name = account.getName();

        if (name.length() > 0) {
            if (name.length() < USER_NAME_MINIMUM_LENGTH || name.length() > USER_NAME_MAXIMUM_LENGTH) {
                errors.rejectValue(NAME, SIZE_OF_NAME);
            }

            String regex = "([0-9|a-z|A-Z|\\_\\.])+";
            boolean matches = Pattern.matches(regex, account.getName());
            if (!matches) {
                errors.rejectValue(NAME, EMPTY_NAME);
            }
        }
    }
}
