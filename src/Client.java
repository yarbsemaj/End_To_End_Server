
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import javax.crypto.Cipher;


/**
 * Created by yarbs on 31/03/2017.
 */
public class Client implements Runnable {
    private Socket socket;
    private BufferedReader receiveRead;
    private OutputStream ostream;
    private PrintWriter pwrite;
    private boolean connectionActive = true;
    private String clientID;

    public Client(Socket socket, String clientID) throws IOException {
        this.clientID = clientID;
        this.socket = socket;
        InputStream istream = socket.getInputStream();
        receiveRead = new BufferedReader(new InputStreamReader(istream));
        ostream = socket.getOutputStream();
        pwrite = new PrintWriter(ostream, true);
    }

    @Override
    public void run() {
        while (connectionActive) {
            try {
                String receiveMessage;
                if ((receiveMessage = receiveRead.readLine()) != null) {

                    GUI.i.addIncoming(receiveMessage);

                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(receiveMessage);
                    JSONObject object = (JSONObject) obj;
                    try {
                        switch (((Long) object.get("PacketType")).intValue()) {
                            case Server.REQUEST_CHAT:
                                newChat(object);
                                break;
                            case Server.KEY_STAGE_1:
                                findChat((String) object.get("ChatID")).keyStage1();
                                break;
                            case Server.KEY_STAGE_2:
                            case Server.MESSAGE:
                                findChat((String) object.get("ChatID")).getOtherMember(this).send(receiveMessage);
                                break;
                        }
                    }catch(NullPointerException e){
                        sendError(Server.INVALID_CHAT_ERROR);
                    }
                }
            } catch (IOException e) {
                try {
                    stop();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() throws IOException {
        connectionActive= false;
        ostream.close();
        pwrite.close();
        receiveRead.close();
        socket.close();
        Server.clients.remove(clientID);
    }

    public void newChat(JSONObject object){
        if(Server.clients.containsKey(object.get("ID"))){
            ArrayList<Client> clients = new ArrayList<>(2);
            clients.add(this);
            Client otherClient = Server.clients.get(object.get("ID"));
            clients.add(otherClient);
            String chatID = RandomString.getSaltString(10);
            Chat chat = new Chat(chatID);
            chat.clients = clients;
            Server.chats.put(chatID,chat);

            chat.requestChat();
            chat.keyStage1();
        }else{
            sendError(Server.NO_USER_ERROR);
        }
    }

    public void send(String sendMessage){
        GUI.i.addOutgoing(sendMessage);

        pwrite.println(sendMessage);
        pwrite.flush();
    }

    public Chat findChat(String chatID){
        Chat chat = Server.chats.get(chatID);
        if(chat.clients.contains(this))
            return Server.chats.get(chatID);
        else
            return null;
    }


    public void sendError(int errorCode){
        JSONObject obj = new JSONObject();

        obj.put("PacketType", Server.ERROR);
        obj.put("ErrorCode", errorCode);

        send(obj.toJSONString());
    }

    public String toString(){
        return clientID;
    }
}
