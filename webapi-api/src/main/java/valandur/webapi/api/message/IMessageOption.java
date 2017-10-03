package valandur.webapi.api.message;

import org.spongepowered.api.text.Text;

public interface IMessageOption {

    /**
     * Gets the key of the message option that is returned to the server
     * @return The key of the message option.
     */
    String getKey();

    /**
     * Gets the value of the message option that is displayed to the user.
     * @return The value of the message option.
     */
    Text getValue();
}
