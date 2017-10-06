package valandur.webapi.json.view.block;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.api.json.BaseView;

import java.util.UUID;

public class BlockSnapshotView extends BaseView<BlockSnapshot> {

    public UUID creator;
    public Location<World> location;
    public BlockState state;


    public BlockSnapshotView(BlockSnapshot value) {
        super(value);

        this.creator = value.getCreator().orElse(null);
        this.location = value.getLocation().orElse(null);
        this.state = value.getState();
    }
}
