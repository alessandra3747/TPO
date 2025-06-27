/**
 *
 *  @author Fus Aleksandra S30395
 *
 */

package zad1;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class ChatClient extends Thread {

    private String host;
    private int port;
    private String id;

    private SocketChannel socketChannel;
    private final StringBuilder chatView;


    public ChatClient(String host, int port, String id) {

        this.host = host;
        this.port = port;
        this.id = id;

        this.chatView = new StringBuilder();
        chatView.append("=== ").append(id).append(" chat view\n");

    }


    public void login() {

        try {

            this.socketChannel = SocketChannel.open(new InetSocketAddress(this.host, this.port));
            this.socketChannel.configureBlocking(false);

            int attempts = 0;
            while (!socketChannel.finishConnect()) {

                Thread.sleep(50);

                if (++attempts > 30) {
                    throw new TimeoutException();
                }

            }

            send("login " + this.id);

            this.start();

        } catch (Exception e) {
            chatView.append("*** ").append(e.toString()).append("\n");
        }

    }


    public void logout() {

        try {

            send("bye");

            Thread.sleep(200);

            this.interrupt();

        } catch (Exception e) {
            chatView.append("*** ").append(e.toString()).append("\n");
        }

    }


    public void send(String req) {

        try {

            ByteBuffer buffer = ByteBuffer.wrap((req + "\n").getBytes(StandardCharsets.UTF_8));
            this.socketChannel.write(buffer);

        } catch (Exception e) {
            chatView.append("*** ").append(e.toString()).append("\n");
        }

    }


    @Override
    public void run() {

        ByteBuffer buffer = ByteBuffer.allocate(1024);


        while (!Thread.currentThread().isInterrupted()) {

            try {

                int bytesRead = socketChannel.read(buffer);

                if (bytesRead > 0) {
                    buffer.flip();
                    CharBuffer decoded = StandardCharsets.UTF_8.decode(buffer);
                    String[] lines = decoded.toString().split("\n");

                    for (String line : lines) {
                        if (!line.trim().isEmpty()) {
                            chatView.append(line.trim()).append("\n");
                        }
                    }
                }

            } catch (ClosedByInterruptException e) {
                break;

            } catch (Exception e) {
                chatView.append("*** ").append(e.toString()).append("\n");
                break;
            }

            buffer.clear();
        }

    }


    public String getChatView() {
        return chatView.toString();
    }

    public String getClientId() {
        return this.id;
    }

}