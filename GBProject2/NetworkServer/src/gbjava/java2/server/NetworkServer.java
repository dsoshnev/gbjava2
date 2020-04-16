package gbjava.java2.server;

import gbjava.java2.client.Command;
import gbjava.java2.client.MessageCommand;
import gbjava.java2.client.UserData;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkServer implements AutoCloseable {

    private final int port;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final AuthService authService;
    private final ExecutorService executorService;

    public NetworkServer(int port) {
        this.port = port;
        this.authService = new BaseAuthService();
        this.executorService = Executors.newCachedThreadPool();
    }

    public void start() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            //System.out.printf("Server is started at %s%n", serverSocket);
            LogService.info("Server is started at %s", serverSocket);
            authService.start();
            while (true) {
                //System.out.println("Waiting client connection...");
                LogService.info("Waiting client connection...");
                Socket clientSocket = serverSocket.accept();
                createClientHandler(clientSocket);
            }
        } catch (IOException e) {
            //System.err.println("Failed to establish network connection");
            LogService.error("Failed to establish network connection");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.close();
        }
    }

    private void createClientHandler(Socket clientSocket) {
        //ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        //clientHandler.run();
        executorService.execute(new ClientHandler(this, clientSocket));
    }

    public AuthService getAuthService() {
        return authService;
    }

    public void sendMessage(Command command, ClientHandler owner) throws IOException {

        MessageCommand messageCommand = (MessageCommand) command.getData();
        UserData toUser = messageCommand.getToUser();
        UserData fromUser = owner.getUserData() ;
        messageCommand.setFromUser(fromUser);
        if(toUser.login.equals("All")) {
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
        UserData toUser = messageCommand.getToUser();
        for (ClientHandler client : clients) {
            if (client.getUserData().login.equals(toUser.login)) {
                client.sendCommand(command);
            }
        }
    }

    private synchronized void updateUsersListMessage(ClientHandler clientHandler) throws IOException {
        List<UserData> users = new ArrayList<>();
        users.add(new UserData("All","All", "All"));
        for (ClientHandler client : clients) {
            users.add(client.getUserData());
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

    @Override
    public void close() throws Exception {

        executorService.shutdown();
        authService.stop();
    }
}