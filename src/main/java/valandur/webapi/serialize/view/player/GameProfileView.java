package valandur.webapi.serialize.view.player;

import org.spongepowered.api.profile.GameProfile;
import valandur.webapi.api.serialize.BaseView;

import java.util.UUID;

public class GameProfileView extends BaseView<GameProfile> {

    public UUID uuid;
    public String name;


    public GameProfileView(GameProfile value) {
        super(value);

        this.uuid = value.getUniqueId();
        this.name = value.getName().orElse(null);
    }
}
