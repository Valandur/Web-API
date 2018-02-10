package valandur.webapi.api.cache.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.command.CommandMapping;
import valandur.webapi.api.cache.ICachedObject;

@ApiModel("Command")
public interface ICachedCommand extends ICachedObject<CommandMapping> {

    @ApiModelProperty(value = "The name of the command, sometimes also referred to as primary alias.", required = true)
    String getName();

    @ApiModelProperty(value = "The description provided with the command", required = true)
    String getDescription();

    @ApiModelProperty(value = "All the aliases that were registered for this command", required = true)
    String[] getAliases();

    @ApiModelProperty(value = "A short description of the usage of this command", required = true)
    String getUsage();

    @ApiModelProperty(value = "Extended help information on the usage of the command", required = true)
    String getHelp();
}
