package io.valandur.webapi;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("webapi")
public class ForgeWebAPIPlugin {

    private final Logger logger = LogManager.getLogger();

    public Logger getLogger() {
        return logger;
    }

    private ForgeWebAPI webapi;

    private long serverStart;

    public long getUptime() {
        return System.currentTimeMillis() - serverStart;
    }

    public ForgeWebAPIPlugin() {
        // Register the setup method for mod loading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
                () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        serverStart = System.currentTimeMillis();

        webapi = new ForgeWebAPI(this);
        webapi.load();
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        webapi.start();
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        webapi.stop();
    }
}
