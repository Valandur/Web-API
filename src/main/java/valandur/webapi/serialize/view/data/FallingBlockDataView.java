package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.manipulator.mutable.entity.FallingBlockData;
import valandur.webapi.serialize.BaseView;

@ApiModel("FallingBlockData")
public class FallingBlockDataView extends BaseView<FallingBlockData> {

    @ApiModelProperty(value = "The state of the falling block", required = true)
    public BlockState state;

    @ApiModelProperty(value = "True if the block can drop as an item, false otherwise", required = true)
    public boolean canDropAsItem;

    @ApiModelProperty(value = "True if the block can hurt entities, false otherwise", required = true)
    public boolean canHurtEntities;

    @ApiModelProperty(value = "True if this falling block can be placed as a normal block, false otherwise", required = true)
    public boolean canPlaceAsBlock;

    @ApiModelProperty(value = "The amount of damage per block this falling block deals", required = true)
    public double fallDamagePerBlock;

    @ApiModelProperty(value = "The amount of time (in ticks) this block has been falling for", required = true)
    public int fallTime;

    @ApiModelProperty(value = "The maximum amount of damage this block can deal", required = true)
    public double maxFallDamage;


    public FallingBlockDataView(FallingBlockData value) {
        super(value);

        this.state = value.blockState().get();
        this.canDropAsItem = value.canDropAsItem().get();
        this.canHurtEntities = value.canHurtEntities().get();
        this.canPlaceAsBlock = value.canPlaceAsBlock().get();
        this.fallDamagePerBlock = value.fallDamagePerBlock().get();
        this.fallTime = value.fallTime().get();
        this.maxFallDamage = value.maxFallDamage().get();
    }
}
