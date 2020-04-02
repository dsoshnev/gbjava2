package gbjava.java2.client;

import java.io.Serializable;

public class UserData implements Serializable {
    public String login;
    public String password;
    public String username;

    public UserData(String login) {
        this.login = login;
    }

    public UserData(String login, String password, String username) {
        this.login = login;
        this.password = password;
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "login='" + login + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
