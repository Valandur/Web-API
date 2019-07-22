package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.SkinData;
import valandur.webapi.serialize.BaseView;

import java.util.UUID;

public class SkinDataView extends BaseView<SkinData> {

    @JsonValue
    public UUID uuid;


    public SkinDataView(SkinData value) {
        super(value);

        this.uuid = value.skinUniqueId().get();
    }
}
