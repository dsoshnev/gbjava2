package gbjava.java2.client;

import java.io.Serializable;

public class AuthCommand implements Serializable {

    private final UserData userData;

    public AuthCommand(String login, String password, String username) {
        this.userData = new UserData(login, password, username);
    }

    public String getLogin() {
        return userData.login;
    }

    public String getPassword() {
        return userData.password;
    }

    public String getUsername() {
        return userData.username;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUsername(String username) {
        this.userData.username = username;
    }

    @Override
    public String toString() {
        return "AuthCommand{" +
                "userData='" + userData + '\'' +
                '}';
    }
}
