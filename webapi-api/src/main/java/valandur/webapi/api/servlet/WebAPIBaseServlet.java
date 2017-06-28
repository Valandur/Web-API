package valandur.webapi.api.servlet;

import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.service.*;

public abstract class WebAPIBaseServlet {

    protected IBlockService blockService;
    protected ICacheService cacheService;
    protected IExtensionService extensionService;
    protected IJsonService jsonService;
    protected IMessageService messageService;
    protected IServerService serverService;
    protected IServletService servletService;
    protected IWebHookService webHookService;


    public WebAPIBaseServlet() {
        blockService = WebAPIAPI.getBlockService().orElse(null);
        cacheService = WebAPIAPI.getCacheService().orElse(null);
        extensionService = WebAPIAPI.getExtensionService().orElse(null);
        jsonService = WebAPIAPI.getJsonService().orElse(null);
        messageService = WebAPIAPI.getMessageService().orElse(null);
        serverService = WebAPIAPI.getServerService().orElse(null);
        servletService = WebAPIAPI.getServletService().orElse(null);
        webHookService = WebAPIAPI.getWebHookService().orElse(null);
    }
}
