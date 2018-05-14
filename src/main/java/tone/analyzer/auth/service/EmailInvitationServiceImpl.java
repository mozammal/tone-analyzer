package tone.analyzer.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import tone.analyzer.domain.entity.EmailInvitation;
import tone.analyzer.domain.repository.EmailInvitationRepository;

/** Created by user on 1/8/2018. */
@Service
public class EmailInvitationServiceImpl implements IEmailInvitationService {

  @Autowired private EmailInvitationRepository emailInvitationRepository;

  @Override
  public EmailInvitation findByToeknAndSenderAndReceiver(
      String token, String sender, String receiver) {
    // return emailInvitationRepository.findByToken(token);
    return emailInvitationRepository.findByTokenAndSenderAndReceiver(token, sender, receiver);
  }

  @Override
  public EmailInvitation findByToken(String token) {
    // return emailInvitationRepository.findByToken(token);
    return emailInvitationRepository.findByToken(token);
  }
}
