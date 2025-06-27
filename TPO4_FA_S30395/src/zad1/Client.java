/**
 *
 *  @author Fus Aleksandra S30395
 *
 */

package zad1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;


public class Client {

    private String host;
    private int port;
    private String id;
    private SocketChannel socketChannel;


    public Client(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }


    public void connect() {

        try {

            socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
            socketChannel.configureBlocking(false);

            while(!socketChannel.finishConnect()) {
                Thread.sleep(10);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }


    public String send(String req) {

        try {

            String message = req + "\n";
            ByteBuffer outBuffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));

            while (outBuffer.hasRemaining()) {
                socketChannel.write(outBuffer);
            }

            ByteBuffer inBuffer = ByteBuffer.allocate(1024);
            StringBuilder response = new StringBuilder();

            boolean messageReceived = false;
            int attempts = 0;
            int maxattempts = 50;

            while (!messageReceived && (attempts < maxattempts)) {

                int bytesRead = socketChannel.read(inBuffer);

                if(bytesRead > 0) {

                    inBuffer.flip();
                    response.append(StandardCharsets.UTF_8.decode(inBuffer));
                    inBuffer.clear();


                    if (response.toString().contains("\n")) {
                        messageReceived = true;
                    }

                } else {

                    Thread.sleep(20);
                    attempts++;

                }

            }

            if(req.trim().equalsIgnoreCase("bye")) {
                socketChannel.close();
            }

            return response.toString();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public String getId() {
        return this.id;
    }


}
