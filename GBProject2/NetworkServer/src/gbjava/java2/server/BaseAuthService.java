package gbjava.java2.server;

import gbjava.java2.client.UserData;

import java.sql.*;
import java.util.*;

public class BaseAuthService implements AuthService {

    private Connection conn;

    private static final List<UserData> users = new ArrayList<>();

    @Override
    public UserData AuthorizeUser(String login, String password) {
        for (UserData user : users) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void setUsername(String login, String nickname) {
        // update DB
    }

    @Override
    public void start() throws Exception {

        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:MyDatabase.db");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM users");
        while (rs.next()) {
            String login = rs.getString(1);
            String password = rs.getString(2);
            String username = rs.getString(3);
            users.add(new UserData(login, password, username));
        }
        //System.out.println("Auth service is started");
        LogService.info("Auth service is started");
    }

    @Override
    public void stop() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //System.out.println("Auth service is stopped");
        LogService.info("Auth service is stopped");
    }
}
