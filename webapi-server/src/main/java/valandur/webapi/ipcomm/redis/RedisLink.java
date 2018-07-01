package valandur.webapi.ipcomm.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import valandur.webapi.ipcomm.IPLink;
import valandur.webapi.ipcomm.IPRequest;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class RedisLink extends IPLink {

    private Jedis jedis;
    private Set<String> servers = new ConcurrentSkipListSet<>();


    @Override
    public void init(ContextHandlerCollection handlers) {
        final RedisLink link = this;

        jedis = new Jedis("localhost");
        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if (channel.equalsIgnoreCase("response")) {
                    link.respond(message);
                } else if (channel.equalsIgnoreCase("connect")) {
                    System.out.println("Connected: " + message);
                    link.servers.add(message);
                } else if (channel.equalsIgnoreCase("disconnect")) {
                    System.out.println("Disconnected: " + message);
                    link.servers.remove(message);
                }
            }
        });
    }

    @Override
    public boolean hasServer(String server) {
        return servers.contains(server);
    }

    @Override
    public void send(String server, IPRequest message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String msg = mapper.writeValueAsString(message);
            jedis.publish("request-" + server, msg);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
