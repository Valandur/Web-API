package valandur.webapi.link.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import valandur.webapi.WebAPI;
import valandur.webapi.link.LinkClient;
import valandur.webapi.link.message.*;

import java.io.IOException;

public class RedisClient extends LinkClient {

    private Jedis jedis;


    public RedisClient(String privateKey) {
        super(privateKey);
    }

    @Override
    public void connect(String target) {
        try {
            jedis = new Jedis(target);

            // Send welcome message
            sendConnect(new ConnectMessage(privateKey));

            jedis.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        RequestMessage req = mapper.readValue(message, RequestMessage.class);
                        ResponseMessage res = WebAPI.emulateRequest(req);
                        sendResponse(res);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, "request-" + id);
        } catch (Exception e) {
            e.printStackTrace();
            jedis = null;
        }
    }

    @Override
    public void disconnect() {
        if (jedis == null) {
            return;
        }

        try {
            sendDisconnect(new DisconnectMessage(privateKey));
            jedis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void sendConnect(ConnectMessage message) {
        send("connect", message);
    }

    @Override
    protected void sendDisconnect(DisconnectMessage message) {
        send("disconnect", message);
    }

    @Override
    protected void sendResponse(ResponseMessage message) {
        send("response", message);
    }

    private void send(String channel, BaseMessage msg) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            jedis.publish(channel, mapper.writeValueAsString(msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
