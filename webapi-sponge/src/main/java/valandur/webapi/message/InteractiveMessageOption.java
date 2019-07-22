package valandur.webapi.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

@ApiModel("InteractiveMessageOption")
public class InteractiveMessageOption {

    private String key;
    /**
     * Gets the key of the message option that is returned to the server
     * @return The key of the message option.
     */
    @ApiModelProperty(value = "The key of the option (this is sent to the WebHook)", required = true)
    public String getKey() {
        return key;
    }

    private String value;
    /**
     * Gets the value of the message option that is displayed to the user.
     * @return The value of the message option.
     */
    @ApiModelProperty(dataType = "string", value = "The value of the option (this is displayed to the player)", required = true)
    @JsonIgnore
    public Text getValue() {
        if (value == null) {
            return null;
        }
        return TextSerializers.FORMATTING_CODE.deserialize(value);
    }

    @JsonProperty("value")
    public String getRawValue() {
        return value;
    }
    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }
}
