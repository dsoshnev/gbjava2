package gbjava.java2.client;

import java.io.Serializable;

public class MessageCommand implements Serializable {
    private String fromUser;
    private final String toUser;
    private final String message;

    public MessageCommand(String fromUser, String toUser, String message) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.message = message;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public String getMessage() {
        return message;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    @Override
    public String toString() {
        return "MessageCommand{" +
                "fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

}
