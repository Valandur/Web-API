package valandur.webapi.ipcomm.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import valandur.webapi.WebAPI;
import valandur.webapi.ipcomm.IPClient;
import valandur.webapi.ipcomm.IPRequest;
import valandur.webapi.ipcomm.IPResponse;
import valandur.webapi.util.Util;

import javax.servlet.ServletException;
import java.io.IOException;

public class RedisClient implements IPClient {

    private String id = Util.generateUniqueId();
    private Jedis jedis;


    @Override
    public void connect(String target) {
        jedis = new Jedis(target);
        jedis.publish("connect", id);
        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    IPRequest req = mapper.readValue(message, IPRequest.class);
                    IPResponse res = WebAPI.emulateRequest(req);
                    jedis.publish("response", mapper.writeValueAsString(res));
                } catch (IOException | ServletException e) {
                    e.printStackTrace();
                }
            }
        }, "request-" + id);
    }
}
