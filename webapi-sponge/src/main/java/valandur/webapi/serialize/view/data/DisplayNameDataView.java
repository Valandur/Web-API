package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.text.Text;
import valandur.webapi.serialize.BaseView;

public class DisplayNameDataView extends BaseView<DisplayNameData> {

    @JsonValue
    public Text name;


    public DisplayNameDataView(DisplayNameData value) {
        super(value);

        this.name = value.displayName().get();
    }
}
