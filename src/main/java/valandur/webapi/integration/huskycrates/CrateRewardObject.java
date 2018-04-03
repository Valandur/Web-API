package valandur.webapi.integration.huskycrates;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import ninja.leaping.configurate.ConfigurationNode;

import static com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include =  JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = CrateRewardObject.class
)
@JsonSubTypes({
        @Type(value = CommandCrateReward.class, name = "COMMAND"),
        @Type(value = ItemCrateReward.class, name = "ITEM"),
})
@ApiModel(
        value = "HuskyCratesCrateRewardObject",
        discriminator = "type",
        subTypes = { CommandCrateReward.class, ItemCrateReward.class })
public abstract class CrateRewardObject {

    public enum CrateRewardObjecType {
        ITEM, COMMAND
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty(value = "The type of reward object", required = true, allowableValues = "ITEM, COMMAND")
    public abstract CrateRewardObjecType getType();


    public CrateRewardObject() {}

    public void saveToNode(ConfigurationNode node) {
        node.getNode("type").setValue(getType().toString().toLowerCase());
    }
}
