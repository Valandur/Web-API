package io.valandur.webapi.spigot.entity;

import io.valandur.webapi.entity.Entity;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.world.Location;
import io.valandur.webapi.spigot.SpigotWebAPI;
import io.valandur.webapi.world.Position;
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
        new Location(
            entity.getWorld().getUID(),
            new Position(entity.getLocation().getX(), entity.getLocation().getY(),
                entity.getLocation().getZ())
        ),
        entity.getName()
    );
  }
}
