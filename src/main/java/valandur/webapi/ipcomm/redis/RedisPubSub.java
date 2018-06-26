package valandur.webapi.ipcomm.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import valandur.webapi.WebAPI;
import valandur.webapi.ipcomm.IPRequest;
import valandur.webapi.ipcomm.IPResponse;

import javax.servlet.ServletException;
import java.io.IOException;

public class RedisPubSub extends JedisPubSub {

    private Jedis jedis;


    public RedisPubSub(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public void onMessage(String channel, String msg) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            IPRequest message = mapper.readValue(msg, IPRequest.class);
            IPResponse res = WebAPI.emulateRequest(message);
            jedis.publish("response", mapper.writeValueAsString(res));
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }
}
