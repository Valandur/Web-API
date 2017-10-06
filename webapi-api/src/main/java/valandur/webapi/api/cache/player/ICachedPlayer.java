package valandur.webapi.api.cache.player;

import org.spongepowered.api.entity.living.player.Player;
import valandur.webapi.api.cache.ICachedObject;

import java.util.UUID;

public interface ICachedPlayer extends ICachedObject<Player> {

    UUID getUUID();

    String getName();
}
