package valandur.webapi.serialize.view.data;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.manipulator.mutable.entity.FallingBlockData;
import valandur.webapi.api.serialize.BaseView;

public class FallingBlockDataView extends BaseView<FallingBlockData> {

    public BlockState state;
    public boolean canDropAsItem;
    public boolean canHurtEntities;
    public boolean canPlaceAsBlock;
    public double fallDamagePerBlock;
    public int fallTime;
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
