package io.valandur.webapi.fabric;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarted;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopping;
import net.minecraft.server.MinecraftServer;

public class FabricWebAPIPlugin implements DedicatedServerModInitializer,
    ServerLifecycleEvents.ServerStarting, ServerStarted, ServerStopping {

  private FabricWebAPI webapi;

  private MinecraftServer server;

  public MinecraftServer getServer() {
    return this.server;
  }

  private long serverStart;

  @Override
  public void onInitializeServer() {
    serverStart = System.currentTimeMillis();

    ServerLifecycleEvents.SERVER_STARTING.register(this);
    ServerLifecycleEvents.SERVER_STARTED.register(this);
    ServerLifecycleEvents.SERVER_STOPPING.register(this);

    webapi = new FabricWebAPI(this);
    webapi.load();
  }

  @Override
  public void onServerStarting(MinecraftServer server) {
    this.server = server;
  }

  @Override
  public void onServerStarted(MinecraftServer server) {
    webapi.start();
  }

  @Override
  public void onServerStopping(MinecraftServer server) {
    webapi.stop();
  }
}
