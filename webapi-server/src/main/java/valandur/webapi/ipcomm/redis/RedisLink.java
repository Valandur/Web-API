package valandur.webapi.ipcomm.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import valandur.webapi.ipcomm.IPLink;
import valandur.webapi.ipcomm.IPRequest;

public class RedisLink extends IPLink {

    private Jedis jedis;

    @Override
    public void init() {
        jedis = new Jedis("localhost");
    }

    @Override
    public Class<RedisServlet> getServletClass() {
        return RedisServlet.class;
    }

    @Override
    public boolean hasServer(String server) {
        return false;
    }

    @Override
    public void send(String server, IPRequest message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String msg = mapper.writeValueAsString(message);
            jedis.publish("request", msg);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
