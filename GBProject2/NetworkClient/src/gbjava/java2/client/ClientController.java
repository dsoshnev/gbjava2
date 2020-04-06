package gbjava.java2.client;

import java.io.IOException;

public class ClientController {

    private final NetworkService networkService;
    private final AuthDialog authDialog;
    private final ClientChat clientChat;

    public ClientController(String serverHost, int serverPort) {
        this.networkService = new NetworkService(serverHost, serverPort);
        this.authDialog = new AuthDialog(this);
        this.clientChat = new ClientChat(this);
    }

    public void runApplication() throws IOException {
        connectToServer();
        runAuthProcess();
    }

    private void runAuthProcess() {
        networkService.setAuthMessageHandler(title -> openChat(title));
        networkService.setErrorMessageHandler(authDialog::showError);
        networkService.setEndMessageHandler(message -> authDialog.onClose());
        authDialog.setVisible(true);
    }

    private void openChat(String title) {
        authDialog.dispose();
        clientChat.setTitle(title);
        networkService.setMessageHandler(clientChat::appendMessage);
        networkService.setUpdateUsersListMessageHandler(clientChat::updateUsersList);
        networkService.setErrorMessageHandler(message -> clientChat.showError(message));
        networkService.setEndMessageHandler(message -> clientChat.onClose());
        clientChat.setVisible(true);
    }

    private void connectToServer() throws IOException {
            networkService.connect();
    }

    public void sendAuthMessage(String login, String password, String username) throws IOException {
        networkService.sendAuthMessage(login, password, username);
    }

    public void sendMessage(UserData toUser, String message) throws IOException {
        networkService.sendMessage(toUser, message);
    }

    public void shutdown() {
        authDialog.dispose();
        clientChat.dispose();
        networkService.closeConnection();
    }

}
