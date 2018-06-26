package valandur.webapi.ipcomm.redis;

import valandur.webapi.ipcomm.IPServlet;

import javax.servlet.http.HttpServlet;

public class RedisServlet extends HttpServlet implements IPServlet<RedisLink> {

    private RedisLink link;


    @Override
    public void init(RedisLink link) {
        this.link = link;
    }
}
