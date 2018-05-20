package valandur.webapi.serialize.view.block;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.serialize.BaseView;

import java.util.UUID;

@ApiModel("BlockSnapshot")
public class BlockSnapshotView extends BaseView<BlockSnapshot> {

    @ApiModelProperty("The creator of this snapshot")
    public UUID creator;

    @ApiModelProperty("The location of the snapshot")
    public Location<World> location;

    @ApiModelProperty(value = "The block state that was captured", required = true)
    public BlockState state;


    public BlockSnapshotView(BlockSnapshot value) {
        super(value);

        this.creator = value.getCreator().orElse(null);
        this.location = value.getLocation().orElse(null);
        this.state = value.getState();
    }
}
