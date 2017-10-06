package valandur.webapi.json.view.item;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import valandur.webapi.api.json.BaseView;

import java.util.List;

public class PotionEffectDataView extends BaseView<PotionEffectData> {

    @JsonValue
    public List<PotionEffect> effects;


    public PotionEffectDataView(PotionEffectData value) {
        super(value);

        this.effects = value.effects().get();
    }
}
