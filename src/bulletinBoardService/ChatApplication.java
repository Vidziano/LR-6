package bulletinBoardService;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.lang.reflect.Proxy;

public class ChatApplication extends JFrame {
    private JTextField groupField;
    private JTextField portField;
    private JTextField nameField;
    private JTextArea messageArea;
    private JTextField messageInput;
    private JButton sendButton;
    private JButton connectButton;
    private JButton disconnectButton;
    private JButton clearButton;
    private Messanger messenger;

    public ChatApplication() {
        createUI();
        setTitle("Чат конференції");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(195, 238, 243));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));
        leftPanel.setBackground(new Color(195, 238, 243));

        JPanel groupPanel = createInputPanel("Група:", groupField = new JTextField("224.0.0.1"), new Color(195, 238, 243));
        JPanel portPanel = createInputPanel("Порт:", portField = new JTextField("3456"), new Color(195, 238, 243));
        JPanel namePanel = createInputPanel("Ім'я:", nameField = new JTextField("User"), new Color(195, 238, 243));

        leftPanel.add(groupPanel);
        leftPanel.add(portPanel);
        leftPanel.add(namePanel);

        mainPanel.add(leftPanel, BorderLayout.WEST);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBackground(new Color(195, 238, 243));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel messageInputPanel = new JPanel(new BorderLayout());
        messageInputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messageInputPanel.setBackground(new Color(195, 238, 243));
        messageInput = new JTextField();
        sendButton = new JButton("Надіслати");
        sendButton.setEnabled(false);
        messageInputPanel.add(messageInput, BorderLayout.CENTER);
        messageInputPanel.add(sendButton, BorderLayout.EAST);

        mainPanel.add(messageInputPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel();
        connectButton = new JButton("З'єднати");
        disconnectButton = new JButton("Від'єднатися");
        clearButton = new JButton("Очистити");
        JButton finishButton = new JButton("Завершити");
        bottomPanel.setBackground(new Color(195, 238, 243));
        bottomPanel.add(connectButton);
        bottomPanel.add(disconnectButton);
        bottomPanel.add(clearButton);
        bottomPanel.add(finishButton);

        finishButton.addActionListener(e -> this.dispose());

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        connectButton.addActionListener(e -> connect());
        disconnectButton.addActionListener(e -> disconnect());
        sendButton.addActionListener(e -> sendMessage());
        clearButton.addActionListener(e -> clearMessages());

        add(mainPanel);
    }


    private JPanel createInputPanel(String labelText, JTextField textField, Color backgroundColor) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBackground(backgroundColor);
        JLabel label = new JLabel(labelText);
        textField.setMaximumSize(new Dimension(200, 30));
        panel.add(label);
        panel.add(textField);
        return panel;
    }

    private void finish() {
        disconnect();
    }

    private void clearMessages() {
        messageArea.setText("");
    }

    private void connect() {
        try {
            InetAddress addr = InetAddress.getByName(groupField.getText());
            int port = Integer.parseInt(portField.getText());
            String name = nameField.getText();

            // Створення інстансу UITasksImpl
            UITasksImpl uiTasksImpl = new UITasksImpl(messageArea, messageInput);
            // Створення динамічного проксі для UITasks
            UITasks uiTasks = (UITasks) Proxy.newProxyInstance(
                    getClass().getClassLoader(),
                    new Class[]{UITasks.class},
                    new EDTInvocationHandler(uiTasksImpl)
            );

            // Передача проксі до MessanderImpl
            messenger = new MessanderImpl(addr, port, name, uiTasks);
            messenger.start();
            sendButton.setEnabled(true);
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Помилка з'єднання: " + e.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void disconnect() {
        if (messenger != null) {
            messenger.stop();
            sendButton.setEnabled(false);
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
        }
    }

    private void sendMessage() {
        if (messenger != null) {
            messenger.send();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String[] names = {"Alex", "Jane", "Bill", "Michael"};
            for (String name : names) {
                ChatApplication chat = new ChatApplication();
                chat.setNameFieldText(name);
                chat.setVisible(true);
            }
        });
    }

    public void setNameFieldText(String text) {
        nameField.setText(text);
    }
}
