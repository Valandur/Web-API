package io.valandur.webapi.fabric;

import static net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.*;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.server.MinecraftServer;

public class FabricWebAPIPlugin implements DedicatedServerModInitializer,
    ServerStarting, ServerStopping {

  private FabricWebAPI webapi;

  private MinecraftServer server;
  public MinecraftServer getServer() {
    return this.server;
  }

  private long serverStart;
  public long getUptime() {
    return System.currentTimeMillis() - serverStart;
  }

  @Override
  public void onInitializeServer() {
    serverStart = System.currentTimeMillis();

    webapi = new FabricWebAPI(this);

    SERVER_STARTING.register(this);
    SERVER_STOPPING.register(this);
  }

  @Override
  public void onServerStarting(MinecraftServer server) {
    this.server = server;
    webapi.start();
  }

  @Override
  public void onServerStopping(MinecraftServer server) {
    webapi.stop();
  }
}
