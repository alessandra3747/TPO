/**
 *
 *  @author Fus Aleksandra S30395
 *
 */

package zad1;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;


public class ClientTask extends FutureTask<String> {


    private ClientTask(Callable<String> callable) {
        super(callable);
    }


    public static ClientTask create(Client c, List<String> reqs, boolean showSendRes) {
        return new ClientTask(() -> {
            c.connect();

            String serverResponse = c.send("login " + c.getId());

            if (showSendRes)
                System.out.println(serverResponse);


            for (String req : reqs) {
                serverResponse = c.send(req);

                if (showSendRes)
                    System.out.println(serverResponse);

            }

            serverResponse = c.send("bye and log transfer");

            if (showSendRes)
                System.out.println(serverResponse);

            return serverResponse;
        });
    }


}
