package valandur.webapi.api.cache.player;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.entity.living.player.Player;
import valandur.webapi.api.cache.ICachedObject;

import java.util.UUID;

@ApiModel("Player")
public interface ICachedPlayer extends ICachedObject<Player> {

    @ApiModelProperty(value = "The unique UUID of this player", required = true)
    UUID getUUID();

    @ApiModelProperty(value = "The players name", required = true)
    String getName();

    @ApiModelProperty(value = "True if the player is online, false otherwise", required = true)
    boolean isOnline();
}
