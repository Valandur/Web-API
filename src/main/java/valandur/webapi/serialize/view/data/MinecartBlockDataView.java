package valandur.webapi.serialize.view.data;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.manipulator.mutable.entity.MinecartBlockData;
import valandur.webapi.api.serialize.BaseView;

public class MinecartBlockDataView extends BaseView<MinecartBlockData> {

    public BlockState state;
    public int offset;


    public MinecartBlockDataView(MinecartBlockData value) {
        super(value);

        this.state = value.block().get();
        this.offset = value.offset().get();
    }
}
