package valandur.webapi.cache.block;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.block.BlockSnapshot;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.world.CachedLocation;

import java.util.UUID;

@ApiModel("BlockSnapshot")
public class CachedBlockSnapshot extends CachedObject<BlockSnapshot> {

    @ApiModelProperty("The creator of this snapshot")
    public UUID creator;

    @ApiModelProperty("The location of the snapshot")
    public CachedLocation location;

    @ApiModelProperty(value = "The block state that was captured", required = true)
    public CachedBlockState state;


    public CachedBlockSnapshot(BlockSnapshot value) {
        super(value);

        this.creator = value.getCreator().orElse(null);
        value.getLocation().ifPresent(l -> this.location = new CachedLocation(l));
        this.state = new CachedBlockState(value.getState());
    }
}
