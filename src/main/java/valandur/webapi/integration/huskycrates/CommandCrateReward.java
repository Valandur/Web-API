package valandur.webapi.integration.huskycrates;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import ninja.leaping.configurate.ConfigurationNode;

@ApiModel(value = "HuskyCratesCommandCrateReward", parent = CrateRewardObject.class)
public class CommandCrateReward extends CrateRewardObject {

    @Override
    public CrateRewardObjecType getType() {
        return CrateRewardObjecType.COMMAND;
    }

    @JsonDeserialize
    private String command;
    @ApiModelProperty(value = "The command executed for the player", required = true)
    public String getCommand() {
        return command;
    }


    public CommandCrateReward() {}
    public CommandCrateReward(String command) {
        this.command = command.startsWith("/") ? command.substring(1) : command;
    }

    @Override
    public void saveToNode(ConfigurationNode node) {
        super.saveToNode(node);
        node.getNode("command").setValue(command);
    }
}
