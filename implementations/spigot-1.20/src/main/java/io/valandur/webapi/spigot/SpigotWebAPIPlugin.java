package io.valandur.webapi.spigot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotWebAPIPlugin extends JavaPlugin implements Listener {

  private SpigotWebAPI webapi;
  private long serverStart;

  public long getUptime() {
    return System.currentTimeMillis() - serverStart;
  }

  @Override
  public void onLoad() {
    serverStart = System.currentTimeMillis();

    webapi = new SpigotWebAPI(this);
    webapi.load();
  }

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(this, this);
    webapi.start();
  }

  @Override
  public void onDisable() {
    webapi.stop();
  }

  @EventHandler
  public void onMessage(final BroadcastMessageEvent event) {

  }

  @EventHandler
  public void onCommand(final ServerCommandEvent event) {

  }

  @EventHandler
  public void onPlayerJoin(final PlayerJoinEvent event) {

  }

  @EventHandler
  public void onPlayerLeave(final PlayerQuitEvent event) {

  }

  @EventHandler
  public void onPlayerKick(final PlayerKickEvent event) {

  }

}
