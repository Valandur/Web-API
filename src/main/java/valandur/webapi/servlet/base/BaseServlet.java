package valandur.webapi.servlet.base;

import valandur.webapi.WebAPI;
import valandur.webapi.block.BlockService;
import valandur.webapi.cache.CacheService;
import valandur.webapi.message.InteractiveMessageService;

public abstract class BaseServlet {

    protected BlockService blockService;
    protected CacheService cacheService;
    protected InteractiveMessageService messageService;
    protected ServletService servletService;


    public BaseServlet() {
        blockService = WebAPI.getBlockService();
        cacheService = WebAPI.getCacheService();
        messageService = WebAPI.getMessageService();
        servletService = WebAPI.getServletService();
    }
}
