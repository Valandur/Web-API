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
