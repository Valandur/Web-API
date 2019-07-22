package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.manipulator.mutable.entity.MinecartBlockData;
import valandur.webapi.serialize.BaseView;

@ApiModel("MinecartBlockData")
public class MinecartBlockDataView extends BaseView<MinecartBlockData> {

    @ApiModelProperty(value = "The current state of the block", required = true)
    public BlockState state;

    @ApiModelProperty(value = "The offset of the block", required = true)
    public int offset;


    public MinecartBlockDataView(MinecartBlockData value) {
        super(value);

        this.state = value.block().get();
        this.offset = value.offset().get();
    }
}
