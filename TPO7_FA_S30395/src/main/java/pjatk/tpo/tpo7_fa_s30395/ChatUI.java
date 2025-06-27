package pjatk.tpo.tpo7_fa_s30395;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class ChatUI {

    private final JPanel mainPanel;
    private final JTextArea chatView;
    private final JTextField messageField;
    private final JButton sendButton;
    private final JButton logoutButton;
    private final DefaultListModel<String> userModel;
    private final JList<String> activeUsersList;
    private final JLabel userLabel;


    public ChatUI(String chatRoomName, String userNickname) {

        // --- Main Panel ---
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(238, 242, 248));


        // --- Chat Panel ---
        JPanel chatPanel = new JPanel(new BorderLayout(18, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(250, 251, 254));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chatPanel.setOpaque(false);
        chatPanel.setPreferredSize(new Dimension(750, 470));
        chatPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));


        // --- Chat Label ---
        JLabel chatLabel = new JLabel("Chat Room: " + chatRoomName);
        chatLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        chatLabel.setHorizontalAlignment(JLabel.CENTER);
        chatLabel.setForeground(new Color(45, 90, 160));

        chatPanel.add(chatLabel, BorderLayout.NORTH);


        // --- User Label ---
        userLabel = new JLabel("Your nickname: " + userNickname);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        userLabel.setHorizontalAlignment(JLabel.RIGHT);
        userLabel.setForeground(new Color(45, 90, 160));


        // --- User Panel ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(chatLabel, BorderLayout.WEST);
        topPanel.add(userLabel, BorderLayout.EAST);

        chatPanel.add(topPanel, BorderLayout.NORTH);


        // --- Center Panel ---
        JPanel centerPanel = new JPanel(new BorderLayout(18, 0));
        centerPanel.setOpaque(false);


        // --- Chat Area ---
        chatView = new JTextArea();
        chatView.setEditable(false);
        chatView.setLineWrap(true);
        chatView.setWrapStyleWord(true);
        chatView.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        chatView.setBackground(new Color(244, 247, 252));
        chatView.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 240), 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JScrollPane chatScroll = new JScrollPane(chatView);
        chatScroll.setBorder(BorderFactory.createEmptyBorder());
        chatScroll.setPreferredSize(new Dimension(410, 200));

        centerPanel.add(chatScroll, BorderLayout.CENTER);


        // --- Active Users List ---
        userModel = new DefaultListModel<>();
        activeUsersList = new JList<>(userModel);
        activeUsersList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        activeUsersList.setBackground(new Color(228, 236, 251));
        activeUsersList.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 230), 1, true));

        JLabel activeUsersLabel = new JLabel("Active Users");
        activeUsersLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        activeUsersLabel.setHorizontalAlignment(JLabel.CENTER);
        activeUsersLabel.setForeground(new Color(45, 90, 160));

        JPanel usersPanel = new JPanel(new BorderLayout(0, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(228, 236, 251));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }
        };
        usersPanel.setOpaque(false);
        usersPanel.setPreferredSize(new Dimension(150, 0));
        usersPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 230), 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        usersPanel.add(activeUsersLabel, BorderLayout.NORTH);
        usersPanel.add(new JScrollPane(activeUsersList), BorderLayout.CENTER);

        centerPanel.add(usersPanel, BorderLayout.EAST);
        chatPanel.add(centerPanel, BorderLayout.CENTER);


        // --- Message box and buttons ---
        JPanel bottomPanel = new JPanel(new BorderLayout(9, 0));
        bottomPanel.setOpaque(false);

        messageField = new JTextField();
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        messageField.setPreferredSize(new Dimension(300, 38));
        messageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 230), 1, true),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
        bottomPanel.add(messageField, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        sendButton.setBackground(new Color(45, 90, 160));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setPreferredSize(new Dimension(90, 38));
        sendButton.setBorder(BorderFactory.createLineBorder(new Color(45, 90, 160), 1, true));

        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setBackground(new Color(230, 74, 90));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(90, 38));
        logoutButton.setBorder(BorderFactory.createLineBorder(new Color(230, 74, 90), 1, true));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 9, 0));
        buttonPanel.setOpaque(false);

        buttonPanel.add(sendButton);
        buttonPanel.add(logoutButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        chatPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(chatPanel);
    }

}