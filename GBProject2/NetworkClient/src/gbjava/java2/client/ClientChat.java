package gbjava.java2.client;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Vector;

public class ClientChat extends JFrame {

    private JPanel mainPanel;
    private JList<String> usersList;
    private JTextField messageTextField;
    private JButton sendButton;
    private JTextArea chatText;

    private ClientController controller;

    public ClientChat(ClientController controller) {
        this.controller = controller;
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
            String username = usersList.getSelectedValue();
            String message = messageTextField.getText().trim();
            if (!message.isEmpty()) {
                if(username == null || username.equals("all")) {
                    controller.sendMessage(null, message);
                } else {
                    controller.sendMessage(username, message);
                }
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
            chatText.append(System.lineSeparator());
        });
    }

    public void updateUsersList(Vector<String> users) {
        SwingUtilities.invokeLater(() -> {
            usersList.setListData(users);
            usersList.updateUI();
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
