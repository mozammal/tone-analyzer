package chat.analyzer.domain.DTO;

import chat.analyzer.domain.model.LoginEvent;
import java.io.Serializable;
import java.util.Set;

/** Created by Dell on 1/29/2018. */
public class AwaitingChatMessageNotificationDetailsDTO implements Serializable {

  private String receiver;

  private Set<LoginEvent> sender;

  public AwaitingChatMessageNotificationDetailsDTO() {}

  public AwaitingChatMessageNotificationDetailsDTO(String receiver, Set<LoginEvent> sender) {
    this.receiver = receiver;
    this.sender = sender;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public Set<LoginEvent> getSender() {
    return sender;
  }

  public void setSender(Set<LoginEvent> sender) {
    this.sender = sender;
  }

  @Override
  public String toString() {
    return "AwaitingChatMessageNotificationDetailsDTO{"
        + "receiver='"
        + receiver
        + '\''
        + ", sender="
        + sender
        + '}';
  }
}