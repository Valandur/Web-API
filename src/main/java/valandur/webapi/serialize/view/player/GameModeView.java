package valandur.webapi.serialize.view.player;

import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import valandur.webapi.api.serialize.BaseView;

public class GameModeView extends BaseView<GameMode> {

    public String id;
    public String name;


    public GameModeView(GameMode value) {
        super(value);

        this.id = value.getId();
        this.name = value.getTranslation().get();
    }
}
