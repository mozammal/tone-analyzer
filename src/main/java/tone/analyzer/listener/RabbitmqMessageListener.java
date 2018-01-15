package tone.analyzer.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import tone.analyzer.domain.entity.EmailInvitation;
import tone.analyzer.domain.model.NewUserInvitationNotification;
import tone.analyzer.service.mail.MailService;

/**
 * Created by Dell on 1/15/2018.
 */

@Service
public class RabbitmqMessageListener {


    public static final String RABBITMQ_QUEUE = "registered.user.email";

    private static Logger logger = LoggerFactory.getLogger(RabbitmqMessageListener.class);

    @Autowired
    private MailService mailService;

    @Value("${mail.from}")
    private String from;

    @RabbitListener(queues = RABBITMQ_QUEUE)
    public void receiveMessage(NewUserInvitationNotification newuser) {

        logger.info("Received message for sending email" + newuser.toString());

        mailService.sendMail(newuser);
    }
}
