package gbjava.java2.server;

import gbjava.java2.client.UserData;

public interface AuthService {
    UserData AuthorizeUser(String login, String password);
    void setUsername(String login, String nickname);
    void start() throws Exception;
    void stop();
}
