package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.ListData;
import valandur.webapi.serialize.BaseView;

import java.util.List;

public class ListDataView extends BaseView<ListData> {

    @JsonValue
    public List<Object> list;


    public ListDataView(ListData value) {
        super(value);

        this.list = value.asList();
    }
}
