package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.manipulator.mutable.item.BlockItemData;
import valandur.webapi.serialize.BaseView;

public class BlockItemDataView extends BaseView<BlockItemData> {

    @JsonValue
    public BlockState state;


    public BlockItemDataView(BlockItemData value) {
        super(value);

        this.state = value.state().get();
    }
}
