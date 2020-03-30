package gbjava.java2.server;

import gbjava.java2.client.Command;
import gbjava.java2.client.MessageCommand;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class NetworkServer {

    private final int port;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final AuthService authService;

    public NetworkServer(int port) {
        this.port = port;
        this.authService = new BaseAuthService();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("Server is started at %s%n", serverSocket);
            authService.start();
            while (true) {
                System.out.println("Waiting client connection...");
                Socket clientSocket = serverSocket.accept();
                createClientHandler(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Failed to establish network connection");
            e.printStackTrace();
        } finally {
            authService.stop();
        }
    }

    private void createClientHandler(Socket clientSocket) {
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        clientHandler.run();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public void sendMessage(Command command, ClientHandler owner) throws IOException {
        MessageCommand messageCommand = (MessageCommand) command.getData();
        messageCommand.setFromUser(owner.getNickname());
        if(messageCommand.getToUser() == null) {
            broadcastMessage(command, owner);
        } else {
            personalMessage(command, owner);
        }
    }

    private synchronized void broadcastMessage(Command command, ClientHandler clientHandler) throws IOException {
        for (ClientHandler client : clients) {
            if (client != clientHandler) {
                client.sendCommand(command);
            }
        }
    }

    private synchronized void personalMessage(Command command, ClientHandler clientHandler) throws IOException {
        MessageCommand messageCommand = (MessageCommand) command.getData();
        for (ClientHandler client : clients) {
            if (client.getNickname().equals(messageCommand.getToUser())) {
                client.sendCommand(command);
            }
        }
    }

    private synchronized void updateUsersListMessage(ClientHandler clientHandler) throws IOException {
        Vector<String> users = new Vector<>();
        users.add("all");
        for (ClientHandler client : clients) {
            users.add(client.getNickname());
        }
        for (ClientHandler client : clients) {
            client.sendCommand(Command.updateUsersListCommand(users));
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) throws IOException {
        clients.add(clientHandler);
        updateUsersListMessage(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) throws IOException {
        clients.remove(clientHandler);
        updateUsersListMessage(clientHandler);
    }
}