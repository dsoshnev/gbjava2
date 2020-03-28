package gbjava.java2.client;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
                controller.shutdown();
            }
        });
    }

    private void addListeners() {
        sendButton.addActionListener(e -> sendMessage());
        messageTextField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String message = messageTextField.getText().trim();
        if (!message.isEmpty()) {
            appendOwnMessage(message);
            String  username = usersList.getSelectedValue();
            if(username == null || username.equals("all")) {
                controller.sendMessage(message);
            } else {
                controller.sendPersonalMessage(username, message);
            }
            messageTextField.setText(null);
        }
    }

    public void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatText.append(message);
            chatText.append(System.lineSeparator());
        });
    }

    private void appendOwnMessage(String message) {
        appendMessage("Я: " + message);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
