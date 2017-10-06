package valandur.webapi.json.view.block;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import valandur.webapi.api.json.JsonDetails;
import valandur.webapi.api.json.BaseView;

import java.util.HashMap;
import java.util.Map;

public class BlockStateView extends BaseView<BlockState> {

    public BlockType type;


    public BlockStateView(BlockState value) {
        super(value);

        this.type = value.getType();
    }

    @JsonDetails
    public Map<String, Object> getData() {
        HashMap<String, Object> data = new HashMap<>();
        for (Map.Entry<BlockTrait<?>, ?> entry : value.getTraitMap().entrySet()) {
            data.put(entry.getKey().getName(), entry.getValue());
        }
        return data;
    }
}
