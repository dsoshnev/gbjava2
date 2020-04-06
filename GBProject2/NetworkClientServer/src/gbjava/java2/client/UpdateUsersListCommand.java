package gbjava.java2.client;

import java.io.Serializable;
import java.util.List;

public class UpdateUsersListCommand implements Serializable {
    private final List<UserData> users;

    public UpdateUsersListCommand(List<UserData> users) {
        this.users = users;
    }

    public List<UserData> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "UpdateUsersListCommand{" +
                "users=" + users +
                '}';
    }
}
