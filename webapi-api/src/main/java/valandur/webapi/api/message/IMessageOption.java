package valandur.webapi.api.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.text.Text;

@ApiModel("MessageOption")
public interface IMessageOption {

    /**
     * Gets the key of the message option that is returned to the server
     * @return The key of the message option.
     */
    @ApiModelProperty(value = "The key of the option (this is sent to the webhook)", required = true)
    String getKey();

    /**
     * Gets the value of the message option that is displayed to the user.
     * @return The value of the message option.
     */
    @ApiModelProperty(
            dataType = "string",
            value = "The value of the option (this is displayed to the player)",
            required = true)
    Text getValue();
}
