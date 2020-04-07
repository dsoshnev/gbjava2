package gbjava.java2.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Timer;

import gbjava.java2.client.Command;
import gbjava.java2.client.AuthCommand;
import gbjava.java2.client.UserData;

public class ClientHandler {

    private static final int AUTHENTICATION_TIMEOUT = 120000;
    private final NetworkServer networkServer;
    private final Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private UserData userData;

    private Date timeStamp;

    public ClientHandler(NetworkServer networkServer, Socket socket) {
        this.networkServer = networkServer;
        this.clientSocket = socket;
        timeStamp = new Date();
        System.out.printf("Client %s connected at %s%n", clientSocket, timeStamp);

    }

    public void run() {
        doHandle(clientSocket);
    }


    private void doHandle(Socket socket) {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    if (authentication()) readMessages();
                } catch (IOException e) {
                    System.err.println("Failed to establish client connection");
                } finally {
                    closeConnection();
                }
            }).start();

        } catch (IOException e) {
            System.out.println("Failed to do Client Handler");
            //e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            networkServer.unsubscribe(this);
            sendCommand(Command.endCommand());
            clientSocket.close();
            System.out.printf("Client %s disconnected successfully at %s%n", clientSocket, new Date());
        } catch (IOException e) {
            System.err.println("Connection closing failed");
        }
    }

    private void readMessages() throws IOException {
        clientSocket.setSoTimeout(0);
        while (true) {
            try {
                Command command = readCommand();
                if (command != null) {
                    switch (command.getType()) {
                        case END:
                            return;
                        case MESSAGE:
                            networkServer.sendMessage(command, this);
                            break;
                    }
                }
            } catch(SocketTimeoutException e) {
                //if timeout not equals 0
            }
        }
    }

    private boolean authentication() throws IOException {
        clientSocket.setSoTimeout(AUTHENTICATION_TIMEOUT);
        while (true) {
            try {
                Command command = readCommand();
                if(command != null) {
                    switch (command.getType()) {
                        case END:
                            return false;
                        case AUTH:
                            AuthCommand authCommand = (AuthCommand) command.getData();
                            UserData userData = networkServer.getAuthService().AuthorizeUser(authCommand.getLogin(), authCommand.getPassword());
                            if (userData != null) {
                                this.userData = userData;
                                userData.username = authCommand.getUsername();
                                networkServer.getAuthService().setUsername(userData.login, userData.username);
                                sendCommand(command);
                                networkServer.sendMessage(Command.messageCommand(new UserData("All"), userData.username + " зашел в чат!"), this);
                                networkServer.subscribe(this);
                                return true;
                            } else {
                                sendCommand(Command.errorCommand("Отсутствует учетная запись по данному логину и паролю!"));
                            }
                            break;
                    }
                }
            } catch (SocketTimeoutException e) {
                if (checkAuthTimeout()) {
                    return false;
                }
            }
        }
    }

    private boolean checkAuthTimeout() {
        long timeout = new Date().getTime() - timeStamp.getTime();
        if(timeout >= AUTHENTICATION_TIMEOUT) {
            return true;
        }
        return false;
    }


    private Command readCommand() throws IOException {
        try {
            Command command = (Command) in.readObject();
            System.out.printf("Read: %s: %s%n", userData, command);
            return command;
        } catch (ClassNotFoundException e) {
            sendCommand(Command.errorCommand("Unknown type of object from client!"));
            return null;
        }
    }

    public void sendCommand(Command command) throws IOException {
        out.writeObject(command);
        System.out.printf("Send: %s: %s%n", userData, command);
    }

    public UserData getUserData() {
        return userData;
    }
}
