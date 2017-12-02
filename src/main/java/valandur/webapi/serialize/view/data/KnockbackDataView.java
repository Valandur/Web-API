package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.KnockbackData;
import valandur.webapi.api.serialize.BaseView;

public class KnockbackDataView extends BaseView<KnockbackData> {

    @JsonValue
    public int knockback;


    public KnockbackDataView(KnockbackData value) {
        super(value);

        this.knockback = value.knockbackStrength().get();
    }
}
