package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.MinecartBlockData;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.block.CachedBlockState;

@ApiModel("MinecartBlockData")
public class CachedMinecartBlockData extends CachedObject<MinecartBlockData> {

    @ApiModelProperty(value = "The current state of the block", required = true)
    public CachedBlockState state;

    @ApiModelProperty(value = "The offset of the block", required = true)
    public int offset;


    public CachedMinecartBlockData(MinecartBlockData value) {
        super(value);

        this.state = new CachedBlockState(value.block().get());
        this.offset = value.offset().get();
    }
}
