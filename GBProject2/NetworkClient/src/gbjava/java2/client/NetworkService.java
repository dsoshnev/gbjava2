package gbjava.java2.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

public class NetworkService {

    private final String host;
    private final int port;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private Consumer<String> messageHandler;
    private AuthEvent successfulAuthEvent;
    private String nickname;

    public NetworkService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        runReadThread();
    }

    private void runReadThread() {
        new Thread(() -> {
            while (true) {
                try {
                    String message = readMessage();
                    if (message.startsWith("/auth")) {
                        String[] messageParts = message.split("\\s+", 2);
                        nickname = messageParts[1];
                        successfulAuthEvent.authIsSuccessful(nickname);
                    }
                    else if (messageHandler != null) {
                        messageHandler.accept(message);
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка: Поток чтения был прерван!");
                    return;
                }
            }
        }).start();
    }

    public void sendAuthMessage(String login, String password) throws IOException {
        sendMessage(String.format("/auth %s %s", login, password));
    }

    public void sendPersonalMessage(String username, String message) throws IOException {
        sendMessage(String.format("/w %s %s", username, message));
    }

    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
        System.out.printf("Send: %s%n", message);
    }

    public String readMessage() throws IOException {
        String message = in.readUTF();
        System.out.printf("Read: %s%n", message);
        return message;
    }

    public void setMessageHandler(Consumer<String> messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void setSuccessfulAuthEvent(AuthEvent successfulAuthEvent) {
        this.successfulAuthEvent = successfulAuthEvent;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
