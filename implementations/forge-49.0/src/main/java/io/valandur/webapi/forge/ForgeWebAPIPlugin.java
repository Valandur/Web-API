package io.valandur.webapi.forge;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ForgeWebAPIPlugin.MODID)
public class ForgeWebAPIPlugin {

    public static final String MODID = "webapi";

    private final long serverStart;

    public long getUptime() {
        return System.currentTimeMillis() - serverStart;
    }

    private ForgeWebAPI webapi;

    private MinecraftServer server;

    public MinecraftServer getServer() {
        return server;
    }

    public ForgeWebAPIPlugin() {
        serverStart = System.currentTimeMillis();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        webapi = new ForgeWebAPI(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        server = event.getServer();

        webapi.start();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        webapi.stop();
    }
}
