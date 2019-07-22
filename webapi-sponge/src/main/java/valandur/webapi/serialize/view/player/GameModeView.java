package valandur.webapi.serialize.view.player;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import valandur.webapi.serialize.BaseView;

@ApiModel("GameMode")
public class GameModeView extends BaseView<GameMode> {

    @ApiModelProperty(value = "The unique id of the game mode", required = true)
    public String id;

    @ApiModelProperty(value = "The name of the game mode", required = true)
    public String name;


    public GameModeView(GameMode value) {
        super(value);

        this.id = value.getId();
        this.name = value.getTranslation().get();
    }
}
