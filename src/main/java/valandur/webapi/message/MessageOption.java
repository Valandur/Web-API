package valandur.webapi.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import valandur.webapi.api.message.IMessageOption;

@JsonDeserialize
public class MessageOption implements IMessageOption {

    @JsonDeserialize
    private String key;
    @Override
    public String getKey() {
        return key;
    }

    @JsonDeserialize
    private String value;
    @Override
    public Text getValue() {
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked(value);
    }
}
