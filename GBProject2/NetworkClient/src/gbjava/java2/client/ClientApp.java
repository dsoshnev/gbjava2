package gbjava.java2.client;

import java.io.IOException;


public class ClientApp {

    private static final int DEFAULT_PORT = 8189;

    public static void main(String[] args) {
        try {
            ClientController clientController = new ClientController("localhost", DEFAULT_PORT);
            clientController.runApplication();
        } catch (IOException e) {
            System.err.println("Failed to connect to server! Please, check you network settings");
        }
    }
}
