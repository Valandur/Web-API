package valandur.webapi.api.servlet;

import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.block.IBlockService;
import valandur.webapi.api.cache.ICacheService;
import valandur.webapi.api.hook.IWebHookService;
import valandur.webapi.api.message.IInteractiveMessageService;
import valandur.webapi.api.permission.IPermissionService;
import valandur.webapi.api.serialize.ISerializeService;
import valandur.webapi.api.server.IServerService;

import javax.servlet.http.HttpServlet;

public abstract class BaseServlet extends HttpServlet {

    protected IBlockService blockService;
    protected ICacheService cacheService;
    protected ISerializeService serializeService;
    protected IInteractiveMessageService messageService;
    protected IPermissionService permissionService;
    protected IServerService serverService;
    protected IServletService servletService;
    protected IWebHookService webHookService;


    public BaseServlet() {
        blockService = WebAPIAPI.getBlockService().orElse(null);
        cacheService = WebAPIAPI.getCacheService().orElse(null);
        serializeService = WebAPIAPI.getJsonService().orElse(null);
        messageService = WebAPIAPI.getMessageService().orElse(null);
        permissionService = WebAPIAPI.getPermissionService().orElse(null);
        serverService = WebAPIAPI.getServerService().orElse(null);
        servletService = WebAPIAPI.getServletService().orElse(null);
        webHookService = WebAPIAPI.getWebHookService().orElse(null);
    }
}
