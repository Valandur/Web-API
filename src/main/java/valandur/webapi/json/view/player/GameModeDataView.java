package valandur.webapi.json.view.player;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.GameModeData;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import valandur.webapi.api.json.BaseView;

public class GameModeDataView extends BaseView<GameModeData> {

    @JsonValue
    public GameMode mode;


    public GameModeDataView(GameModeData value) {
        super(value);

        this.mode = value.type().get();
    }
}
