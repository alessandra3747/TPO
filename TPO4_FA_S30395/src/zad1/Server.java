/**
 *
 *  @author Fus Aleksandra S30395
 *
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Server {

    private Selector selector;

    private Thread serverThread;

    private final Map<SocketChannel, StringBuilder> partialMessages = new HashMap<>();
    private final Map<String, StringBuilder> userLogs = new HashMap<>();
    private final StringBuilder serverLog = new StringBuilder();


    public Server(String host, int port) {

        try {

            this.selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(host, port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void startServer() {

        serverThread = new Thread(() -> {

            try {

                while (!Thread.currentThread().isInterrupted()) {

                    selector.select();
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = keys.iterator();

                    while (iter.hasNext()) {

                        SelectionKey key = iter.next();
                        iter.remove();

                        if (key.isAcceptable()) {

                            acceptClient(key);

                        } else if (key.isReadable()) {

                            readFromClient(key);

                        }

                    }

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
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

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int readBytes = clientChannel.read(buffer);

        if (readBytes == -1) {
            clientChannel.close();
            key.cancel();

            return;
        }

        buffer.flip();
        String received = StandardCharsets.UTF_8.decode(buffer).toString();
        StringBuilder clientStringBuilder = partialMessages.get(clientChannel);
        clientStringBuilder.append(received);

        int index;

        while( (index = clientStringBuilder.indexOf("\n")) != -1 ) {

            String request = clientStringBuilder.substring(0, index).trim();

            clientStringBuilder.delete(0, index + 1);

            handleRequest(clientChannel, request);

        }

    }


    private void handleRequest(SocketChannel clientChannel, String request) throws IOException {

        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        String response = "";

        if (request.contains("login")) {

            String clientName = request.split(" ")[1];
            clientChannel.keyFor(selector).attach(clientName);

            userLogs.put(clientName, new StringBuilder());
            userLogs.get(clientName).append("=== ")
                    .append(clientName)
                    .append(" log start ===\nlogged in\n");

            serverLog.append(clientName)
                    .append(" logged in at ")
                    .append(timestamp)
                    .append("\n");

            response = "logged in\n";

        } else if (request.contains("bye")) {

            String clientName = (String) clientChannel.keyFor(selector).attachment();

            userLogs.get(clientName).append("logged out\n=== ")
                    .append(clientName)
                    .append(" log end ===\n");


            serverLog.append(clientName).append(" logged out at ")
                    .append(timestamp)
                    .append("\n");

            response = userLogs.get(clientName).toString();

            ByteBuffer buffer = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));

            clientChannel.write(buffer);

        } else {

            String clientName = (String) clientChannel.keyFor(selector).attachment();

            if (clientName != null && !clientName.trim().isEmpty()) {

                serverLog.append(clientName)
                        .append(" request ")
                        .append(timestamp)
                        .append(": \"")
                        .append(request)
                        .append("\"\n");

                String[] parts = request.split(" +");

                String result = Time.passed(parts[0], parts[1]);

                userLogs.get(clientName).append("Request: ")
                        .append(request)
                        .append("\nResult:\n")
                        .append(result)
                        .append("\n");

                response = result + "\n";
            }

            ByteBuffer buffer = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));
            clientChannel.write(buffer);

        }


    }


    public void stopServer() {

        serverThread.interrupt();
        selector.wakeup();

        try {
            serverThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    public String getServerLog() {
        return serverLog.toString();
    }


}

