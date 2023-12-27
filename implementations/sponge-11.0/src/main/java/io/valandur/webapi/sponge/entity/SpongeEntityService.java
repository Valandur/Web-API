package io.valandur.webapi.sponge.entity;

import io.valandur.webapi.entity.Entity;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.sponge.SpongeWebAPI;
import io.valandur.webapi.world.Location;
import io.valandur.webapi.world.Position;
import java.util.ArrayList;
import java.util.Collection;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.api.world.server.WorldManager;

public class SpongeEntityService extends EntityService<SpongeWebAPI> {

  private final WorldManager worldManager;

  public SpongeEntityService(SpongeWebAPI webapi) {
    super(webapi);

    this.worldManager = Sponge.server().worldManager();
  }

  @Override
  public Collection<Entity> getEntities() {
    var entities = new ArrayList<Entity>();
    for (var world : worldManager.worlds()) {
      entities.addAll(world.entities().stream().map(this::toEntity).toList());
    }
    return entities;
  }

  private Entity toEntity(org.spongepowered.api.entity.Entity entity) {
    return new Entity(
        entity.uniqueId(),
        entity.type().key(RegistryTypes.ENTITY_TYPE).asString(),
        new Location(
            ((ServerWorld) entity.world()).uniqueId(),
            new Position(entity.location().x(), entity.location().y(), entity.location().z())
        ),
        PlainTextComponentSerializer.plainText().serialize(entity.displayName().get())
    );
  }
}
