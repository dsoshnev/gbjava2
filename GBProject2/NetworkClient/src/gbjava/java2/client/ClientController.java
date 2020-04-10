package gbjava.java2.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientController {

    private final int MAX_HISTORY_MESSAGES = 5;

    private final NetworkService networkService;
    private final HistoryService historyService;
    private final AuthDialog authDialog;
    private final ClientChat clientChat;

    private final String[] forbiddenWords = {"дурак", "дура"};
    private List<String> messages;


    public ClientController(String serverHost, int serverPort) {
        this.networkService = new NetworkService(serverHost, serverPort);
        this.historyService = new HistoryService();
        this.authDialog = new AuthDialog(this);
        this.clientChat = new ClientChat(this);
        this.messages = new ArrayList<>();
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


    private void openChat(UserData userData) {
        authDialog.dispose();
        networkService.setMessageHandler(this::appendMessage);
        networkService.setUpdateUsersListMessageHandler(clientChat::updateUsersList);
        networkService.setErrorMessageHandler(clientChat::showError);
        networkService.setEndMessageHandler(message -> clientChat.onClose());
        clientChat.setTitle(userData.username);

        // History loading
        historyService.initHistory(userData.login);
        clientChat.initChat(historyService.load(MAX_HISTORY_MESSAGES));

        clientChat.setVisible(true);
    }

    private void connectToServer() throws IOException {
            networkService.connect();
    }

    public void sendAuthMessage(String login, String password, String username) throws IOException {
        networkService.sendAuthMessage(login, password, username);
    }

    public void sendMessage(UserData toUser, String message) throws IOException {
        String s = replaceForbiddenWords(message);
        networkService.sendMessage(toUser, s);

        // Append Own Message
        appendMessage("Я: " + s);
    }

    private String replaceForbiddenWords(String message) {
        for (String word : forbiddenWords) {
            Pattern pattern = Pattern.compile(String.format("\\b%s\\b", word));
            Matcher matcher = pattern.matcher(message);
            message = matcher.replaceAll(String.format("[%s]", word.replaceAll(".", "*")));
        }
        return message;
    }

    public void appendMessage(String message) {
        clientChat.appendMessage(message);
        messages.add(message);
    }

    public void shutdown() {
        historyService.write(messages);
        authDialog.dispose();
        clientChat.dispose();
        networkService.closeConnection();
    }

}
