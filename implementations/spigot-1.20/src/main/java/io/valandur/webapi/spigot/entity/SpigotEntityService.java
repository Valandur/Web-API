package io.valandur.webapi.spigot.entity;

import io.valandur.webapi.entity.Entity;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.entity.Location;
import io.valandur.webapi.spigot.SpigotWebAPI;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.Server;

public class SpigotEntityService extends EntityService<SpigotWebAPI> {

  private final Server server;

  public SpigotEntityService(SpigotWebAPI webapi) {
    super(webapi);

    this.server = webapi.getPlugin().getServer();
  }

  @Override
  public Collection<Entity> getEntities() {
    var entities = new ArrayList<Entity>();
    for (var world : server.getWorlds()) {
      entities.addAll(world.getEntities().stream().map(this::toEntity).toList());
    }
    return entities;
  }

  private Entity toEntity(org.bukkit.entity.Entity entity) {
    return new Entity(
        entity.getUniqueId(),
        entity.getType().name(),
        entity.getWorld().getUID(),
        new Location(entity.getLocation().getX(), entity.getLocation().getY(),
            entity.getLocation().getZ()),
        entity.getName()
    );
  }
}
