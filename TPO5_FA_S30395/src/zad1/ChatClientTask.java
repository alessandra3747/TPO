/**
 *
 *  @author Fus Aleksandra S30395
 *
 */

package zad1;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ChatClientTask extends FutureTask<String> {

    private final ChatClient chatClient;

    private ChatClientTask(Callable<String> callable, ChatClient chatClient) {
        super(callable);
        this.chatClient = chatClient;
    }

    public static ChatClientTask create(ChatClient c, List<String> msgs, int wait) {
        return new ChatClientTask(() -> {

            String clientName = c.getClientId();

            c.login();

            Thread.sleep(100);

            if (wait != 0) {
                Thread.sleep(wait);
            }

            try {
                for (String msg : msgs) {
                    c.send(msg);

                    if (wait != 0) {
                        Thread.sleep(wait);
                    }
                }

                c.logout();

                if (wait != 0) {
                    Thread.sleep(wait);
                }

            } catch (Exception e) {
                return e.getMessage();
            }

            return clientName + " completed";

        }, c);
    }

    public ChatClient getClient() {
        return this.chatClient;
    }

}