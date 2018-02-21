package valandur.webapi.api.cache.world;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;
import valandur.webapi.api.cache.ICachedObject;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Optional;
import java.util.UUID;

@ApiModel(value = "World", subTypes = ICachedWorldFull.class)
public interface ICachedWorld extends ICachedObject<World> {

    @ApiModelProperty(value = "The unique UUID that identifies this world", required = true)
    UUID getUUID();

    @ApiModelProperty(value = "The name of this world", required = true)
    String getName();

    @ApiModelProperty(value = "True if the world is loaded, false otherwise", required = true)
    boolean isLoaded();

    @ApiModelProperty(hidden = true)
    Optional<WorldProperties> getLiveProps();
}
