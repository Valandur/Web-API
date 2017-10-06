package valandur.webapi.api.servlet;

import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.block.IBlockService;
import valandur.webapi.api.cache.ICacheService;
import valandur.webapi.api.extension.IExtensionService;
import valandur.webapi.api.hook.IWebHookService;
import valandur.webapi.api.json.IJsonService;
import valandur.webapi.api.message.IMessageService;
import valandur.webapi.api.permission.IPermissionService;
import valandur.webapi.api.server.IServerService;

public abstract class BaseServlet {

    protected IBlockService blockService;
    protected ICacheService cacheService;
    protected IExtensionService extensionService;
    protected IJsonService jsonService;
    protected IMessageService messageService;
    protected IPermissionService permissionService;
    protected IServerService serverService;
    protected IServletService servletService;
    protected IWebHookService webHookService;


    public BaseServlet() {
        blockService = WebAPIAPI.getBlockService().orElse(null);
        cacheService = WebAPIAPI.getCacheService().orElse(null);
        extensionService = WebAPIAPI.getExtensionService().orElse(null);
        jsonService = WebAPIAPI.getJsonService().orElse(null);
        messageService = WebAPIAPI.getMessageService().orElse(null);
        permissionService = WebAPIAPI.getPermissionService().orElse(null);
        serverService = WebAPIAPI.getServerService().orElse(null);
        servletService = WebAPIAPI.getServletService().orElse(null);
        webHookService = WebAPIAPI.getWebHookService().orElse(null);
    }
}
