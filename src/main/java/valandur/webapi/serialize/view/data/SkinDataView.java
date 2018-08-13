package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.SkinData;
import valandur.webapi.serialize.BaseView;

import java.util.UUID;

public class SkinDataView extends BaseView<SkinData> {

    @JsonValue
    public String name;


    public SkinDataView(SkinData value) {
        super(value);

        this.name = value.skin().get().getName();
    }
}
