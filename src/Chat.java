import encription.PrimitiveRoot;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by yarbs on 31/03/2017.
 */

public class Chat {
    public ArrayList<Client> clients = new ArrayList<>(2);

    public String chatID;

    public Chat(String chatID){
        this.chatID = chatID;
    }

    public void requestChat(){
        JSONObject obj = new JSONObject();
        obj.put("PacketType", Server.REQUEST_CHAT);
        obj.put("ChatID", chatID);
        obj.put("Members",parseArrayList(clients));

        sendToAll (obj.toJSONString());
    }

    public void keyStage1(){
        int p = PrimitiveRoot.getRandomPrime(10000);
        LinkedList<Integer> roots =PrimitiveRoot.findRoot(p);

        int q = roots.get(new Random().nextInt(roots.size()));

        JSONObject obj = new JSONObject();

        obj.put("PacketType", Server.KEY_STAGE_1);
        obj.put("p", p);
        obj.put("q", q);
        obj.put("ChatID", chatID);

        sendToAll(obj.toJSONString());
    }

    public Client getOtherMember(Client client){
        for(Client tempClient:clients){
            if(client != tempClient){
                return tempClient;
            }
        }
        return null;
    }

    public void sendToAll(String data){
        for(Client client:clients){
            client.send(data);
        }
    }

    public JSONArray parseArrayList(ArrayList<Client> arrayList){
        JSONArray jsonArray = new JSONArray();
        for(Client client:arrayList){
            jsonArray.add(client.toString());
        }
        return jsonArray;
    }
}
