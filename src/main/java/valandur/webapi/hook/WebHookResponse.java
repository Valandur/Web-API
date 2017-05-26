package valandur.webapi.hook;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

@JsonDeserialize
public class WebHookResponse {

    public enum FormattingStyle {
        CODE, JSON,
    }

    @JsonDeserialize
    private String message;
    public Text getMessage() {
        switch (formatting) {
            case CODE:
                return TextSerializers.FORMATTING_CODE.deserialize(message);

            case JSON:
                return TextSerializers.JSON.deserialize(message);

            default:
                return Text.of(message);
        }
    }

    @JsonDeserialize
    private FormattingStyle formatting;

    @JsonDeserialize
    private String[] targets;
    public String[] getTargets() {
        return targets;
    }
}
