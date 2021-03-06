package chat.analyzer;

import static org.mockito.Mockito.mock;

import chat.analyzer.service.EmailInvitationServiceImpl;
import chat.analyzer.auth.service.UserDetailsServiceImpl;
import chat.analyzer.dao.UserAccountDao;
import chat.analyzer.service.invitation.UserInvitationService;
import chat.analyzer.service.tone.recognizer.ToneAnalyzerService;
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

  @Bean
  @Primary
  UserDetailsServiceImpl userDetailsService() {
    return Mockito.mock(UserDetailsServiceImpl.class);
  }

  @Bean
  @Primary
  UserInvitationService userInvitationService() {
    return Mockito.mock(UserInvitationService.class);
  }

  @Bean
  @Primary
  ToneAnalyzerService toneAnalyzerService() {
    return Mockito.mock(ToneAnalyzerService.class);
  }
}
