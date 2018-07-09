package valandur.webapi.cache.misc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.command.CommandSource;
import valandur.webapi.cache.CachedObject;

@ApiModel("CommandSource")
public class CachedCommandSource extends CachedObject<CommandSource> {

    @ApiModelProperty(value = "The unique id of this command source", required = true)
    public String id;

    @ApiModelProperty(value = "The name of this command source", required = true)
    public String name;


    public CachedCommandSource(CommandSource value) {
        super(value);

        this.id = value.getIdentifier();
        this.name = value.getName();
    }
}
