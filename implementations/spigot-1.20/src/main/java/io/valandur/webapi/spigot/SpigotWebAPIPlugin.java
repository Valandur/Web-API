package io.valandur.webapi.spigot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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

    getServer().getPluginManager().registerEvents(this, this);
  }

  @Override
  public void onEnable() {
    webapi.start();
  }

  @Override
  public void onDisable() {
    webapi.stop();
  }

  @EventHandler
  public void onMessage(final AsyncPlayerChatEvent event) {

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
