package valandur.webapi.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import valandur.webapi.api.message.IInteractiveMessageOption;

public class InteractiveMessageOption implements IInteractiveMessageOption {

    private String key;
    @Override
    public String getKey() {
        return key;
    }

    private String value;
    @Override
    @JsonIgnore
    public Text getValue() {
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked(value);
    }
    @JsonProperty("value")
    public String getRawValue() {
        return value;
    }
}
