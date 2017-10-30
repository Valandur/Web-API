package valandur.webapi.integration.huskycrates;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ninja.leaping.configurate.ConfigurationNode;

public class CommandCrateReward extends CrateRewardObject {

    @Override
    public CrateRewardObjecType getType() {
        return CrateRewardObjecType.COMMAND;
    }

    @JsonDeserialize
    private String command;
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
