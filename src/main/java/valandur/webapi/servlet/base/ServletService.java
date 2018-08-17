package valandur.webapi.servlet.base;

import org.slf4j.Logger;
import valandur.webapi.WebAPI;
import valandur.webapi.config.BaseConfig;
import valandur.webapi.config.ServletsConfig;
import valandur.webapi.integration.activetime.ActiveTimeServlet;
import valandur.webapi.integration.cmdscheduler.CmdSchedulerServlet;
import valandur.webapi.integration.gwmcrates.GWMCratesServlet;
import valandur.webapi.integration.huskycrates.HuskyCratesServlet;
import valandur.webapi.integration.mmcrestrict.MMCRestrictServlet;
import valandur.webapi.integration.mmctickets.MMCTicketsServlet;
import valandur.webapi.integration.nucleus.NucleusServlet;
import valandur.webapi.integration.redprotect.RedProtectServlet;
import valandur.webapi.integration.universalmarket.UniversalMarketServlet;
import valandur.webapi.integration.villagershops.VShopServlet;
import valandur.webapi.integration.webbooks.WebBooksServlet;
import valandur.webapi.servlet.*;

import javax.ws.rs.Path;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * This service allows registering servlets with the Web-API, which it will serve for clients.
 * Your servlet must inherit from {@link BaseServlet} and have the
 * {@link javax.ws.rs.Path} annotation specifying the base path at which the servlet will
 * be accessible.
 */
public class ServletService {

    private static final String configFileName = "servlets.conf";

    private Map<String, Class<? extends BaseServlet>> servletClasses = new HashMap<>();


    public void init() {
        Logger logger = WebAPI.getLogger();

        logger.info("Registering servlets...");

        java.nio.file.Path configPath = WebAPI.getConfigPath().resolve(configFileName).normalize();
        ServletsConfig config = BaseConfig.load(configPath, new ServletsConfig());

        servletClasses.clear();

        if (config.Block) registerServlet(BlockServlet.class);
        if (config.Chunk) registerServlet(ChunkServlet.class);
        if (config.Cmd) registerServlet(CmdServlet.class);
        if (config.Economy) registerServlet(EconomyServlet.class);
        if (config.Entity) registerServlet(EntityServlet.class);
        if (config.History) registerServlet(HistoryServlet.class);
        if (config.Info) registerServlet(InfoServlet.class);
        if (config.InteractiveMessage) registerServlet(InteractiveMessageServlet.class);
        if (config.Map) registerServlet(MapServlet.class);
        if (config.Permission) registerServlet(PermissionServlet.class);
        if (config.Player) registerServlet(PlayerServlet.class);
        if (config.Plugin) registerServlet(PluginServlet.class);
        if (config.Recipe) registerServlet(RecipeServlet.class);
        if (config.Registry) registerServlet(RegistryServlet.class);
        if (config.Server) registerServlet(ServerServlet.class);
        if (config.TileEntity) registerServlet(TileEntityServlet.class);
        if (config.User) registerServlet(UserServlet.class);
        if (config.World) registerServlet(WorldServlet.class);

        // Other plugin integrations
        if (config.integrations.ActiveTime) {
            try {
                Class.forName("com.mcsimonflash.sponge.activetime.ActiveTime");
                logger.info("  Integrating with ActiveTime...");
                registerServlet(ActiveTimeServlet.class);
            } catch (ClassNotFoundException ignored) { }
        }

        if (config.integrations.CmdScheduler) {
            try {
                Class.forName("com.mcsimonflash.sponge.cmdscheduler.CmdScheduler");
                logger.info("  Integrating with CmdScheduler...");
                registerServlet(CmdSchedulerServlet.class);
            } catch (ClassNotFoundException ignored) { }
        }

        if (config.integrations.GWMCrates) {
            try {
                Class.forName("org.gwmdevelopments.sponge_plugin.crates.GWMCrates");
                logger.info("  Integrating with GWMCrates...");
                registerServlet(GWMCratesServlet.class);
            } catch (ClassNotFoundException ignored) {
            }
        }

        if (config.integrations.HuskyCrates) {
            try {
                Class.forName("com.codehusky.huskycrates.HuskyCrates");
                logger.info("  Integrating with HuskyCrates...");
                registerServlet(HuskyCratesServlet.class);
            } catch (ClassNotFoundException ignored) { }
        }

        if (config.integrations.MMCRestrict) {
            try {
                Class.forName("net.moddedminecraft.mmcrestrict.Main");
                logger.info("  Integrating with MMCRestrict...");
                registerServlet(MMCRestrictServlet.class);
            } catch (ClassNotFoundException ignored) { }
        }

        if (config.integrations.MMCTickets) {
            try {
                Class.forName("net.moddedminecraft.mmctickets.Main");
                logger.info("  Integrating with MMCTickets...");
                registerServlet(MMCTicketsServlet.class);
            } catch (ClassNotFoundException ignored) { }
        }

        if (config.integrations.Nucleus) {
            try {
                Class.forName("io.github.nucleuspowered.nucleus.api.NucleusAPI");
                logger.info("  Integrating with Nucleus...");
                registerServlet(NucleusServlet.class);
            } catch (ClassNotFoundException ignored) { }
        }

        if (config.integrations.RedProtect) {
            try {
                Class.forName("br.net.fabiozumbi12.RedProtect.Sponge.RedProtect");
                logger.info("  Integrating with RedProtect...");
                registerServlet(RedProtectServlet.class);
            } catch (ClassNotFoundException ignored) { }
        }

        if (config.integrations.UniversalMarket) {
            try {
                Class.forName("com.xwaffle.universalmarket.UniversalMarket");
                logger.info("  Integrating with UniversalMarket...");
                registerServlet(UniversalMarketServlet.class);
            } catch (ClassNotFoundException ignored) { }
        }

        if (config.integrations.VillagerShops) {
            try {
                Class.forName("de.dosmike.sponge.vshop.VillagerShops");
                logger.info("  Integrating with VillagerShops...");
                registerServlet(VShopServlet.class);
            } catch (ClassNotFoundException ignored) { }
        }

        if (config.integrations.WebBooks) {
            try {
                Class.forName("de.dosmike.sponge.WebBooks.WebBooks");
                logger.info("  Integrating with WebBooks...");
                registerServlet(WebBooksServlet.class);
            } catch (ClassNotFoundException ignored) { }
        }
    }

    /**
     * Register a servlet with the WebAPI, which will give it a separate base address
     * @param servlet The class of servlet to register. The WebAPI will create an instance when starting. Make
     *                sure to provide an empty constructor.
     */
    public void registerServlet(Class<? extends BaseServlet> servlet) {
        Logger logger = WebAPI.getLogger();

        if (!servlet.isAnnotationPresent(Path.class)) {
            logger.error("Servlet " + servlet.getName() + " is missing @Path annotation");
            return;
        }

        Path info = servlet.getAnnotation(Path.class);
        String basePath = info.value();
        if (basePath.endsWith("/"))
            basePath = basePath.substring(0, basePath.length() - 1);
        if (!basePath.startsWith("/"))
            basePath = "/" + basePath;

        try {
            Method m = servlet.getMethod("onRegister");
            m.invoke(null);
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            WebAPI.sentryCapture(e);
        }

        servletClasses.put(basePath, servlet);
    }

    /**
     * Gets a map of all available base paths mapped to the servlets that implement them.
     * @return A map from base path to implementing servlet class.
     */
    public Map<String, Class<? extends BaseServlet>> getRegisteredServlets() {
        return servletClasses;
    }
}
