package valandur.webapi.serialize.view.misc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.command.CommandSource;
import valandur.webapi.serialize.BaseView;

@ApiModel("CommandSource")
public class CommandSourceView extends BaseView<CommandSource> {

    @ApiModelProperty(value = "The unique id of this command source", required = true)
    public String id;

    @ApiModelProperty(value = "The name of this command source", required = true)
    public String name;


    public CommandSourceView(CommandSource value) {
        super(value);

        this.id = value.getIdentifier();
        this.name = value.getName();
    }
}
