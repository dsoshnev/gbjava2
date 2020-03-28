package gbjava.java2.server;

public interface AuthService {
    String getUsernameByLoginAndPassword(String login, String password);

    void start();
    void stop();
}
