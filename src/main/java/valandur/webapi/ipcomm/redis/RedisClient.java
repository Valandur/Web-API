package valandur.webapi.ipcomm.redis;

import redis.clients.jedis.Jedis;
import valandur.webapi.ipcomm.IPClient;

public class RedisClient implements IPClient {

    private Jedis jedis;
    private RedisPubSub pubSub;


    @Override
    public void connect(String target) {
        jedis = new Jedis(target);
        pubSub = new RedisPubSub(new Jedis(target)); // Pass in another instance to use for sending
        jedis.subscribe(pubSub, "request");
    }
}
