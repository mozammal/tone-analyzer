package chat.analyzer;

import static org.mockito.Mockito.mock;

import chat.analyzer.auth.service.EmailInvitationServiceImpl;
import chat.analyzer.dao.UserAccountDao;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import chat.analyzer.capcha.service.GoogleReCaptchaService;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class ChatAnalyzerBootTestConfig {

  @Bean
  @Primary
  GoogleReCaptchaService reCaptchaService() {
    return Mockito.mock(GoogleReCaptchaService.class);
  }

  @Bean
  @Primary
  UserAccountDao userAccountDao() {
    return Mockito.mock(UserAccountDao.class);
  }

  @Bean
  @Primary
  EmailInvitationServiceImpl emailInvitationService() {
    return Mockito.mock(EmailInvitationServiceImpl.class);
  }
}
