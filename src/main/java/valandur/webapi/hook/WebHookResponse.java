package valandur.webapi.hook;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

@JsonDeserialize
public class WebHookResponse {

    @JsonDeserialize
    private String message;
    public Text getMessage() {
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked(message);
    }

    @JsonDeserialize
    private String[] targets;
    public String[] getTargets() {
        return targets;
    }
}
