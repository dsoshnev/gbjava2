package gbjava.java2.client;

import java.io.IOException;


public class ClientApp {

    private static final int DEFAULT_PORT = 8189;
    private static  final String DEFAULT_HOST = "localhost";

    public static void main(String[] args) {
        try {
            ClientController clientController = new ClientController(DEFAULT_HOST, DEFAULT_PORT);
            clientController.runApplication();
        } catch (IOException e) {
            System.err.println("Failed to connect to server! Please, check you network settings");
        }
    }
}
