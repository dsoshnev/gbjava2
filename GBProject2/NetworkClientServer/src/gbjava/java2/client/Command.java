package gbjava.java2.client;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class Command implements Serializable {

    private CommandType type;
    private Object data;

    public Object getData() { return data; };

    public CommandType getType() { return type; }

    public static Command authCommand(String login, String password, String username) {
        Command command = new Command();
        command.type = CommandType.AUTH;
        command.data = new AuthCommand(login, password, username);
        return command;
    }

    /*public static Command authErrorCommand(String errorMessage) {
        Command command = new Command();
        command.type = CommandType.AUTH_ERROR;
        command.data = new ErrorCommand(errorMessage);
        return command;
    }*/

    public static Command errorCommand(String errorMessage) {
        Command command = new Command();
        command.type = CommandType.ERROR;
        command.data = new ErrorCommand(errorMessage);
        return command;
    }

    public static Command messageCommand(UserData toUser, String message) {
        Command command = new Command();
        command.type = CommandType.MESSAGE;
        command.data = new MessageCommand(null, toUser, message);
        return command;
    }

    public static Command updateUsersListCommand(List<UserData> users) {
        Command command = new Command();
        command.type = CommandType.UPDATE_USERS_LIST;
        command.data = new UpdateUsersListCommand(users);
        return command;
    }

    public static Command endCommand() {
        Command command = new Command();
        command.type = CommandType.END;
        return command;
    }

    @Override
    public String toString() {
        return "Command{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }
}
