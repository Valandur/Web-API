package valandur.webapi.serialize.view.misc;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import org.spongepowered.api.text.Text;
import valandur.webapi.api.serialize.BaseView;

@ApiModel("Text")
public class TextView extends BaseView<Text> {

    @JsonValue
    public String text;


    public TextView(Text value) {
        super(value);

        this.text = value.toPlain();
    }
}
