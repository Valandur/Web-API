package valandur.webapi.serialize.view.block;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.manipulator.DataManipulator;
import valandur.webapi.WebAPI;
import valandur.webapi.api.serialize.BaseView;
import valandur.webapi.api.serialize.JsonDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BlockStateView extends BaseView<BlockState> {

    public BlockType type;


    public BlockStateView(BlockState value) {
        super(value);

        this.type = value.getType();
    }

    @JsonDetails
    public Map<String, Object> getData() {
        HashMap<String, Object> data = new HashMap<>();
        // Add traits
        for (Map.Entry<BlockTrait<?>, ?> entry : value.getTraitMap().entrySet()) {
            data.put(entry.getKey().getName(), entry.getValue());
        }
        // Add data
        Map<String, Class<? extends DataManipulator<?, ?>>> supData = WebAPI.getSerializeService().getSupportedData();
        for (Map.Entry<String, Class<? extends DataManipulator<?, ?>>> entry : supData.entrySet()) {
            try {
                Optional<?> m = value.getManipulators().stream()
                        .filter(i -> i.asMutable().getClass().equals(entry.getValue()))
                        .findFirst();

                if (!m.isPresent())
                    continue;

                data.put(entry.getKey(), ((DataManipulator) m.get()).copy());
            } catch (IllegalArgumentException | IllegalStateException ignored) {
            }
        }
        return data;
    }
}
