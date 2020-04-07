package gbjava.java2.client;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ClientChat extends JFrame {

    private JPanel mainPanel;

    private JList<String> usersList;
    private List<UserData> usersData;

    private JTextField messageTextField;
    private JButton sendButton;

    private JTextArea chatText;
    private List<String> messages;

    private ClientController controller;

    public ClientChat(ClientController controller) {
        this.controller = controller;
        this.messages = new ArrayList<>();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(640, 480);
        setLocationRelativeTo(null);
        setContentPane(mainPanel);
        addListeners();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });
    }

    private void addListeners() {
        sendButton.addActionListener(e -> sendMessage());
        messageTextField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        try {
            int index = usersList.getSelectedIndex();
            if (index == -1) {
                index = 0;
            }
            UserData toUser = usersData.get(index);
            String message = messageTextField.getText().trim();
            if (!message.isEmpty()) {
                controller.sendMessage(toUser, message);
                appendOwnMessage(message);
                messageTextField.setText(null);
            }
        } catch (IOException e) {
            showError("Ошибка при отправке сообщения");
        }
    }

    public void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatText.append(message);
            messages.add(message);
            chatText.append(System.lineSeparator());
        });
    }

    public void initChat(List<String> messages) {
        //SwingUtilities.invokeLater(() -> {
        for (String message : messages) {
            chatText.append(message);
            chatText.append(System.lineSeparator());
        }
       // });
    }

    public List<String> getMessages() {
        //String[] lines = chatText.getText().split("\\n");
        return messages;
    }

    public void updateUsersList(List<UserData> users) {
        SwingUtilities.invokeLater(() -> {
            usersData = users;
            Vector<String> v = new Vector<>();
            for (UserData user : users) {
                v.add(user.username);
            }
            usersList.setListData(v);
            if(isVisible()) {
                usersList.updateUI();
            }
        });
    }

    private void appendOwnMessage(String message) {
        appendMessage("Я: " + message);
    }

    public void onClose() {
        this.dispose();
        controller.shutdown();
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
