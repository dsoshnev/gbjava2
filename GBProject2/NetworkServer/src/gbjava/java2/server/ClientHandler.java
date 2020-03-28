package gbjava.java2.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.Arrays;

public class ClientHandler {

    private final NetworkServer networkServer;
    private final Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickname;

    public ClientHandler(NetworkServer networkServer, Socket socket) {
        this.networkServer = networkServer;
        this.clientSocket = socket;
    }

    public void run() {
        doHandle(clientSocket);
    }

    private void doHandle(Socket socket) {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    System.out.println("Соединение с клиентом " + nickname + " было закрыто");
                } finally {
                    closeConnection();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        networkServer.unsubscribe(this);
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages() throws IOException {
        System.out.println("Чтение сообщений...");
        while (true) {
            // "/end"
            String message = readMessage();
            if ("/end".equals(message)) {
                return;
            }
            // "/w username message"
            if (message.startsWith("/w")) {
                String[] messageParts = message.split("\\s+", 3);
                networkServer.personalMessage(messageParts[1], nickname + ": " + messageParts[2], this);
            } else {
                networkServer.broadcastMessage(nickname + ": " + message, this);
            }
        }
    }

    private void authentication() throws IOException {
        System.out.println("Аутентфикация...");
        while (true) {
            String message = readMessage();
            // "/auth login password"
            if (message.startsWith("/auth")) {
                String[] messageParts = message.split("\\s+", 3);
                String username = networkServer.getAuthService().getUsernameByLoginAndPassword(messageParts[1], messageParts[2]);
                if (username == null) {
                    sendMessage("Отсутствует учетная запись по данному логину и паролю!");
                } else {
                    nickname = username;
                    networkServer.broadcastMessage(nickname + " зашел в чат!", this);
                    sendMessage("/auth " + nickname);
                    networkServer.subscribe(this);
                    break;
                }
            }
        }
    }

    public String readMessage() throws IOException {
        String message = in.readUTF();
        System.out.printf("Получение: %s: %s%n", nickname, message);
        return message;
    }

    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
        System.out.printf("Отправка: %s: %s%n", nickname, message);
    }

    public String getNickname() {
        return nickname;
    }
}
