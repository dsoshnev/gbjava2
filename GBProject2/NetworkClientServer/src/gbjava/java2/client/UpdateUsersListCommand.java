package gbjava.java2.client;

import java.io.Serializable;
import java.util.Vector;

public class UpdateUsersListCommand implements Serializable {
    private final Vector<String> users;

    public UpdateUsersListCommand(Vector<String> users) {
        this.users = users;
    }

    public Vector<String> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "UpdateUsersListCommand{" +
                "users=" + users +
                '}';
    }
}
