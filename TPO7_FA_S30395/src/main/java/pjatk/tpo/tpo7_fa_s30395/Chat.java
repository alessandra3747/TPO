package pjatk.tpo.tpo7_fa_s30395;

import org.apache.kafka.clients.producer.ProducerRecord;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Chat extends JFrame {

    private final ChatUI ui;
    private final MessageConsumer messageConsumer;
    private final PresenceConsumer presenceConsumer;
    private final String userNickname;
    private final String topic;
    private final String presenceTopic = "chat-presence";

    private ExecutorService executor;
    private volatile boolean running = true;


    public Chat(String userNickname, String topic) {
        super("Chat: " + topic + " | " + userNickname);

        this.userNickname = userNickname;
        this.topic = topic;


        // --- UI setup ---
        ui = new ChatUI(topic, userNickname);
        setContentPane(ui.getMainPanel());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(850, 570);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);


        // --- Business Logic ---
        ui.getUserModel().clear();
        addUser(userNickname);


        PresenceProducer.send(new ProducerRecord<>(presenceTopic, userNickname, "online"));

        messageConsumer = new MessageConsumer(topic, userNickname);
        presenceConsumer = new PresenceConsumer(presenceTopic);


        MessageProducer.send(new ProducerRecord<>(topic, userNickname + " logged in"));

        executor = Executors.newFixedThreadPool(2);
        executor.submit(this::pollMessages);
        executor.submit(this::pollPresence);

        ui.getSendButton().addActionListener(e -> sendMessage());
        ui.getMessageField().addActionListener(e -> ui.getSendButton().doClick());
        ui.getLogoutButton().addActionListener(e -> logout());


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logout();
                super.windowClosing(e);
            }
        });

    }


    private void pollMessages() {
        while (running) {
            messageConsumer.kafkaConsumer.poll(Duration.ofSeconds(1)).forEach(m -> {
                String message = m.value();
                if (message.contains("logged in")) {
                    String newUser = message.split(" ")[0];
                    appendChatInfo(newUser + " has logged in.");
                }
                else if (message.contains("logged out")) {
                    String leavingUser = message.split(" ")[0];
                    appendChatInfo(leavingUser + " has logged out.");
                }
                else {
                    appendChatInfo(message);
                }
            });
        }
    }


    private void pollPresence() {
        while (running) {
            presenceConsumer.kafkaConsumer.poll(Duration.ofSeconds(1)).forEach(m -> {
                String nick = m.key();
                String status = m.value();
                if ("online".equals(status)) {
                    addUser(nick);
                } else if ("offline".equals(status)) {
                    removeUser(nick);
                }
            });
        }
    }


    private void sendMessage() {
        String text = ui.getMessageField().getText().trim();
        if (!text.isEmpty()) {
            String msg = getTime() + " | " + userNickname + ": " + text;
            MessageProducer.send(new ProducerRecord<>(topic, msg));
            ui.getMessageField().setText("");
        }
    }


    private void logout() {
        MessageProducer.send(new ProducerRecord<>(topic, userNickname + " logged out"));
        PresenceProducer.send(new ProducerRecord<>(presenceTopic, userNickname, "offline"));
        removeUser(userNickname);
        running = false;
        executor.shutdownNow();
        dispose();
        System.exit(0);
    }


    private void addUser(String user) {
        DefaultListModel<String> model = ui.getUserModel();
        if (!model.contains(user)) {
            model.addElement(user);
        }
    }


    private void removeUser(String user) {
        DefaultListModel<String> model = ui.getUserModel();
        model.removeElement(user);
    }


    private void appendChatInfo(String info) {
        ui.getChatView().append(info + "\n");
    }


    private String getTime() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int min = now.getMinute();
        return hour + ":" + (min < 10 ? "0" : "") + min;
    }

}