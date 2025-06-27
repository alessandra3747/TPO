/**
 *
 *  @author Fus Aleksandra S30395
 *
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChatServer {

    private Selector selector;
    private Thread serverThread;
    private ServerSocketChannel serverSocketChannel;

    private final Map<SocketChannel, StringBuilder> partialMessages = new HashMap<>();
    private final Map<SocketChannel, String> clients = new LinkedHashMap<>();
    private final StringBuilder serverLog = new StringBuilder();

    public ChatServer(String host, int port) {

        try {

            this.selector = Selector.open();
            this.serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(host, port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startServer() {

        serverThread = new Thread(() -> {

            System.out.println("Server started\n");

            while (!Thread.currentThread().isInterrupted()) {

                try {
                    selector.select();

                    if (Thread.interrupted()) {
                        break;
                    }

                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

                    while (iter.hasNext()) {

                        SelectionKey key = iter.next();
                        iter.remove();

                        if (key.isAcceptable()) {

                            acceptClient(key);

                        } else if (key.isReadable()) {

                            readFromClient(key);

                        }

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });

        serverThread.start();

    }

    private void acceptClient(SelectionKey key) throws IOException {

        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        SocketChannel clientChannel = serverSocketChannel.accept();

        clientChannel.configureBlocking(false);

        clientChannel.register(selector, SelectionKey.OP_READ);

        partialMessages.put(clientChannel, new StringBuilder());

    }

    private void readFromClient(SelectionKey key) throws IOException {

        SocketChannel clientChannel = (SocketChannel) key.channel();

        if (!clientChannel.isOpen() || clientChannel.socket().isClosed()) {
            return;
        }

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int readBytes = clientChannel.read(buffer);

        if (readBytes == -1) {
            return;
        }

        buffer.flip();
        String received = StandardCharsets.UTF_8.decode(buffer).toString();
        StringBuilder clientStringBuilder = partialMessages.get(clientChannel);
        clientStringBuilder.append(received);

        int index;

        while ((index = clientStringBuilder.indexOf("\n")) != -1) {

            String request = clientStringBuilder.substring(0, index).trim();

            clientStringBuilder.delete(0, index + 1);

            handleRequest(clientChannel, request);

        }

    }

    private void handleRequest(SocketChannel clientChannel, String request) {

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        if (request.contains("login")) {

            String clientId = request.split(" ")[1];

            clients.put(clientChannel, clientId);

            broadcast(clientId + " logged in");

            serverLog.append(timeFormat.format(LocalTime.now()))
                    .append(" ")
                    .append(clientId)
                    .append(" logged in\n");

        } else if (request.contains("bye")) {

            String clientId = clients.get(clientChannel);

            broadcast(clientId + " logged out");

            serverLog.append(timeFormat.format(LocalTime.now()))
                    .append(" ")
                    .append(clientId)
                    .append(" logged out\n");

            disconnect(clientChannel);

        } else {

            String clientId = clients.get(clientChannel);

            broadcast(clientId + ": " + request);

            serverLog.append(timeFormat.format(LocalTime.now()))
                    .append(" ")
                    .append(clientId)
                    .append(": ")
                    .append(request)
                    .append("\n");

        }

    }

    private void broadcast(String message) {

        ByteBuffer buffer = ByteBuffer.wrap((message + "\n").getBytes(StandardCharsets.UTF_8));

        for (SocketChannel socketChannel : clients.keySet()) {

            try {

                socketChannel.write(buffer.duplicate());
                buffer.rewind();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void disconnect(SocketChannel clientChannel) {

        try {
            clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        partialMessages.remove(clientChannel);
        clients.remove(clientChannel);
    }

    public void stopServer() {

        System.out.println("Server stopped");
        serverThread.interrupt();
        selector.wakeup();

        try {
            serverThread.join();

            if (selector != null && selector.isOpen()) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
                try {
                    serverSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public String getServerLog() {
        return serverLog.toString();
    }

}