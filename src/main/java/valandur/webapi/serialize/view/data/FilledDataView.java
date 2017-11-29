package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.FilledData;
import valandur.webapi.api.serialize.BaseView;

public class FilledDataView extends BaseView<FilledData> {

    @JsonValue
    public boolean filled;


    public FilledDataView(FilledData value) {
        super(value);

        this.filled = value.filled().get();
    }
}
