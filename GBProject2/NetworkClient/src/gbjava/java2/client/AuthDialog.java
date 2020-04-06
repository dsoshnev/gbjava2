package gbjava.java2.client;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class AuthDialog extends JFrame {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField loginText;
    private JPasswordField passwordText;
    private JTextField usernameText;

    private ClientController controller;

    public AuthDialog(ClientController controller) {
        this.controller = controller;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        setSize(400, 250);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });
        addListeners();
    }

    private void addListeners() {
        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onClose());
    }

    private void onOK() {
        String login = loginText.getText().trim();
        String password = new String(passwordText.getPassword()).trim();
        String username = usernameText.getText().trim();
        try {
            controller.sendAuthMessage(login, password, username);
        } catch (IOException e) {
            showError("Ошибка при попытке аутентификации!");
        }
    }

    public void onClose() {
        this.dispose();
        controller.shutdown();
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

}
