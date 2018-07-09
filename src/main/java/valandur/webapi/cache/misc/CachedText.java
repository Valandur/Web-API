package valandur.webapi.cache.misc;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import org.spongepowered.api.text.Text;
import valandur.webapi.cache.CachedObject;

@ApiModel("Text")
public class CachedText extends CachedObject<Text> {

    @JsonValue
    public String text;


    public CachedText(Text value) {
        super(value);

        this.text = value.toPlain();
    }
}
