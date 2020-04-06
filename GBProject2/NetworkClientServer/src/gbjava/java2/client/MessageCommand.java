package gbjava.java2.client;

import java.io.Serializable;

public class MessageCommand implements Serializable {
    private UserData fromUser;
    private final UserData toUser;
    private final String message;

    public MessageCommand(UserData fromUser, UserData toUser, String message) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.message = message;
    }

    public UserData getFromUser() {
        return fromUser;
    }

    public UserData getToUser() {
        return toUser;
    }

    public String getMessage() {
        return message;
    }

    public void setFromUser(UserData fromUser) {
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
