package valandur.webapi.cache.player;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.profile.GameProfile;
import valandur.webapi.cache.CachedObject;

import java.util.UUID;

@ApiModel("GameProfile")
public class CachedGameProfile extends CachedObject<GameProfile> {

    @ApiModelProperty(value = "The UUID of the game profile", required = true)
    public UUID uuid;

    @ApiModelProperty(value = "The name of the player this game profile belongs to", required = true)
    public String name;


    public CachedGameProfile(GameProfile value) {
        super(value);

        this.uuid = value.getUniqueId();
        this.name = value.getName().orElse(null);
    }
}
