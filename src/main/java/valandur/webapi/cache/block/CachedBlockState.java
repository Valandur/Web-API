package valandur.webapi.cache.block;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.manipulator.DataManipulator;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.serialize.JsonDetails;

import javax.ws.rs.NotFoundException;
import java.util.Map;
import java.util.Optional;

@ApiModel("BlockState")
public class CachedBlockState extends CachedObject<BlockState> {

    private CachedCatalogType<BlockType> type;
    @ApiModelProperty(value = "The type of block this block state is from")
    public CachedCatalogType<BlockType> getType() {
        return type;
    }

    @JsonDetails
    @ApiModelProperty("Additional data attached to the block state")
    public Map<String, Object> getData() {
        return data;
    }


    public CachedBlockState(BlockState value) {
        super(value);

        this.type = new CachedCatalogType<>(value.getType());

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
    }

    @Override
    public BlockState getLive() {
        Optional<BlockType> optType = Sponge.getRegistry().getType(BlockType.class, type.getId());
        if (!optType.isPresent()) {
            throw new NotFoundException("Could not find block type " + type.getId());
        }
        BlockType type = optType.get();

        BlockState state = type.getDefaultState();
        return state;
    }
}
