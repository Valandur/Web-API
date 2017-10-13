package valandur.webapi.serialize.view.item;

import org.spongepowered.api.effect.potion.PotionEffectType;
import valandur.webapi.api.serialize.BaseView;

public class PotionEffectTypeView extends BaseView<PotionEffectType> {

    public String id;
    public String name;


    public PotionEffectTypeView(PotionEffectType value) {
        super(value);

        this.id = value.getId();
        this.name = value.getTranslation().get();
    }
}
