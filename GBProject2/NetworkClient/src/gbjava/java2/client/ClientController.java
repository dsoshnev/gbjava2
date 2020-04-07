package gbjava.java2.client;

import java.io.IOException;
import java.util.List;

public class ClientController {

    private final int MAX_HISTORY_MESSAGES = 5;

    private final NetworkService networkService;
    private final HistoryService historyService;
    private final AuthDialog authDialog;
    private final ClientChat clientChat;

    public ClientController(String serverHost, int serverPort) {
        this.networkService = new NetworkService(serverHost, serverPort);
        this.historyService = new HistoryService();
        this.authDialog = new AuthDialog(this);
        this.clientChat = new ClientChat(this);
    }

    public void runApplication() throws IOException {
        connectToServer();
        runAuthProcess();
    }

    private void runAuthProcess() {
        networkService.setAuthMessageHandler(this::openChat);
        networkService.setErrorMessageHandler(authDialog::showError);
        networkService.setEndMessageHandler(message -> authDialog.onClose());
        authDialog.setVisible(true);
    }

    private void initHistory(String login) {
        historyService.initHistory(login);
    }
    private void openChat(UserData userData) {
        authDialog.dispose();
        //System.out.println("Чат открывается");
        networkService.setMessageHandler(clientChat::appendMessage);
        networkService.setUpdateUsersListMessageHandler(clientChat::updateUsersList);
        networkService.setErrorMessageHandler(message -> clientChat.showError(message));
        networkService.setEndMessageHandler(message -> clientChat.onClose());
        //System.out.println("История загружается");
        clientChat.setTitle(userData.username);
        historyService.initHistory(userData.login);
        clientChat.initChat(historyService.load(MAX_HISTORY_MESSAGES));
        //System.out.println("Чат отображается");
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
        historyService.write(clientChat.getMessages());
        authDialog.dispose();
        clientChat.dispose();
        networkService.closeConnection();
    }

}
