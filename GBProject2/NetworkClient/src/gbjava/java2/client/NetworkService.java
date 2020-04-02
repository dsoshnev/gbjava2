package gbjava.java2.client;

import java.io.*;
import java.net.Socket;
import java.util.Vector;
import java.util.function.Consumer;

public class NetworkService {

    private final String host;
    private final int port;

    private Socket socket;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Consumer<String> messageHandler;
    private Consumer<Vector<String>> updateUsersListMessageHandler;
    private Consumer<String> errorMessageHandler;
    private Consumer<String> authMessageHandler;
    private Consumer<String> endMessageHandler;

    private String nickname;

    public NetworkService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        runReadThread();
    }

    private void runReadThread() {
        new Thread(() -> {
            while (true) {
                try {
                    Command command = readCommand();
                    if(command != null) {
                        switch (command.getType()) {
                            case AUTH:
                                AuthCommand aCommand = (AuthCommand) command.getData();
                                authMessageHandler.accept(aCommand.getUsername());
                                break;
                            case MESSAGE:
                                if (messageHandler != null) {
                                    MessageCommand mCommand = (MessageCommand) command.getData();
                                    messageHandler.accept(String.format("%s: %s", mCommand.getFromUser(), mCommand.getMessage()));
                                }
                                break;
                            case ERROR:
                                ErrorCommand errorCommand = (ErrorCommand) command.getData();
                                errorMessageHandler.accept(errorCommand.getError());
                                break;
                            case UPDATE_USERS_LIST:
                                UpdateUsersListCommand uCommand = (UpdateUsersListCommand) command.getData();
                                updateUsersListMessageHandler.accept(uCommand.getUsers());
                                break;
                            case END:
                                endMessageHandler.accept(null);
                                return;
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Failed to establish server connection");
                    return;
                }
            }
        }).start();
    }

    public void sendAuthMessage(String login, String password) throws IOException {
        sendCommand(Command.authCommand(login, password));
    }

    public void sendMessage(String username, String message) throws IOException {
        sendCommand(Command.messageCommand(username, message));
    }

    private void sendCommand(Command command) throws IOException {
        out.writeObject(command);
        System.out.printf("Send: %s%n", command);
    }

    private Command readCommand() throws IOException {
        try {
            Command command = (Command) in.readObject();
            System.out.printf("Read: %s%n", command);
            return command;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public void setMessageHandler(Consumer<String> messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void setUpdateUsersListMessageHandler(Consumer<Vector<String>> updateUsersListMessageHandler) {
        this.updateUsersListMessageHandler = updateUsersListMessageHandler;
    }

    public void setErrorMessageHandler(Consumer<String> errorMessageHandler) {
        this.errorMessageHandler = errorMessageHandler;
    }

    public void setAuthMessageHandler(Consumer<String> authMessageHandler) {
        this.authMessageHandler = authMessageHandler;
    }

    public void setEndMessageHandler(Consumer<String> endMessageHandler) {
        this.endMessageHandler = endMessageHandler;
    }

    public void closeConnection() {
        try {
            sendCommand(Command.endCommand());
            socket.close();
        } catch (IOException e) {
            System.err.println("Server connection closed");
        }
    }
}
