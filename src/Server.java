import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by yarbs on 31/03/2017.
 */
public class Server implements Runnable {

    public static final int RETURN_ID = 2;
    public static final int REQUEST_CHAT = 3;
    public static final int KEY_STAGE_1 = 4;
    public static final int KEY_STAGE_2 = 5;
    public static final int MESSAGE = 6;
    public static final int ERROR = -1;

    public static final int NO_USER_ERROR = 1;
    public static final int INVALID_CHAT_ERROR = 2;

    public static HashMap<String, Client> clients = new HashMap<>();
    public static HashMap<String, Chat> chats = new HashMap<>();

    private boolean acceptConnection = true;

    private ServerSocket sersock;

    public Server(int port) {
        try {
            sersock = new ServerSocket(port);
            System.out.println("Server  ready for chatting");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (acceptConnection) {
                Socket socket = sersock.accept();
                String ID = RandomString.getSaltString(10);
                Client client = new Client(socket, ID);

                Thread t1 = new Thread(client);
                t1.start();

                clients.put(ID, client);

                JSONObject obj = new JSONObject();

                obj.put("PacketType", RETURN_ID);
                obj.put("ID", ID);

                client.send(obj.toJSONString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() throws IOException {
        acceptConnection = false;
        Iterator it = clients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ((Client)pair.getValue()).stop();
            System.out.println("Client Stopped: "+pair.getKey());
        }
        sersock.close();
    }

}
