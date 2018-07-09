package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.SlimeData;
import valandur.webapi.cache.CachedObject;

@ApiModel("SlimeData")
public class CachedSlimeData extends CachedObject<SlimeData> {

    @ApiModelProperty(value = "The size of the slime entity", required = true)
    public int size;


    public CachedSlimeData(SlimeData value) {
        super(value);

        this.size = value.size().get();
    }
}
