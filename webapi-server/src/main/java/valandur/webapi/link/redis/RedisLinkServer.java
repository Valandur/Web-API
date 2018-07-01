package valandur.webapi.link.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import valandur.webapi.link.LinkServer;
import valandur.webapi.link.message.ConnectMessage;
import valandur.webapi.link.message.DisconnectMessage;
import valandur.webapi.link.message.RequestMessage;
import valandur.webapi.link.message.ResponseMessage;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class RedisLinkServer extends LinkServer {

    private Jedis jedis;
    private Set<String> servers = new ConcurrentSkipListSet<>();


    public RedisLinkServer(Map<String, String> serverKeys) {
        super(serverKeys);
    }

    @Override
    public void init(ContextHandlerCollection handlers) {
        final RedisLinkServer link = this;

        jedis = new Jedis("localhost");
        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    ConnectMessage msg = mapper.readValue(message, ConnectMessage.class);
                    link.addServer(msg.getKey());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "connect");

        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    DisconnectMessage msg = mapper.readValue(message, DisconnectMessage.class);
                    link.removeServer(msg.getKey());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "disconnect");

        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    ResponseMessage msg = mapper.readValue(message, ResponseMessage.class);
                    link.respond(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "response");
    }

    @Override
    public void sendRequest(String serverName, RequestMessage message) {
        String serverKey = serverKeys.get(serverName);

        try {
            ObjectMapper mapper = new ObjectMapper();
            String msg = mapper.writeValueAsString(message);
            jedis.publish("request-" + serverKey, msg);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
