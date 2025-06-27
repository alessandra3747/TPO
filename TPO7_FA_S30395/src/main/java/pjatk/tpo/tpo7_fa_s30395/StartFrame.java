package pjatk.tpo.tpo7_fa_s30395;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class StartFrame extends JFrame {

    private JTextField nicknameField;
    private JTextField topicField;
    private JButton joinButton;


    public StartFrame() {

        setTitle("Start Chat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setResizable(false);
        setLocationRelativeTo(null);


        // --- Main panel ---
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);


        // --- Login panel ---
        JPanel loginPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setColor(new Color(245, 247, 250));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 22, 22);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        loginPanel.setOpaque(false);
        loginPanel.setPreferredSize(new Dimension(330, 350));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 15);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 15);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 16);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 8, 8, 8);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;


        // --- Title ---
        gbc.gridy = 0;
        JLabel title = new JLabel("Welcome to Chat");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setForeground(new Color(33, 64, 128));
        loginPanel.add(title, gbc);


        // --- Nickname Label ---
        gbc.gridy = 1;
        JLabel nicknameLabel = new JLabel("Nickname:");
        nicknameLabel.setFont(labelFont);
        loginPanel.add(nicknameLabel, gbc);


        // --- Nickname Field ---
        gbc.gridy = 2;
        nicknameField = new JTextField();
        nicknameField.setFont(fieldFont);
        nicknameField.setPreferredSize(new Dimension(200, 30));
        nicknameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 230), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        loginPanel.add(nicknameField, gbc);


        // --- Room Label ---
        gbc.gridy = 3;
        JLabel topicLabel = new JLabel("Room:");
        topicLabel.setFont(labelFont);
        loginPanel.add(topicLabel, gbc);


        // --- Room Field ---
        gbc.gridy = 4;
        topicField = new JTextField("chat");
        topicField.setFont(fieldFont);
        topicField.setPreferredSize(new Dimension(200, 30));
        topicField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 230), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        loginPanel.add(topicField, gbc);


        // --- Join Button ---
        gbc.gridy = 5;
        joinButton = new JButton("Join");
        joinButton.setFont(buttonFont);
        joinButton.setBackground(new Color(33, 120, 220));
        joinButton.setForeground(Color.WHITE);
        joinButton.setFocusPainted(false);
        joinButton.setPreferredSize(new Dimension(120, 36));
        joinButton.setBorder(BorderFactory.createLineBorder(new Color(33, 120, 220), 1, true));
        loginPanel.add(joinButton, gbc);


        mainPanel.add(loginPanel);

        setContentPane(mainPanel);

        // --- Button Action Listener ---
        joinButton.addActionListener((ActionEvent e) -> {
            String userNickname = nicknameField.getText().trim();
            String topic = topicField.getText().trim();
            if (!userNickname.isEmpty() && !topic.isEmpty()) {
                new Chat(userNickname, topic);
                dispose();
            }
        });

        setVisible(true);
    }
}