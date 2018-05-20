package valandur.webapi.serialize.view.player;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.util.RespawnLocation;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.serialize.BaseView;

public class RespawnLocationView extends BaseView<RespawnLocation> {

    @JsonValue
    public Location<World> loc;


    public RespawnLocationView(RespawnLocation value) {
        super(value);

        loc = value.asLocation().orElse(null);
    }
}
