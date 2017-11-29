package valandur.webapi.serialize.view.misc;

import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.util.Color;
import valandur.webapi.api.serialize.BaseView;

public class DyeColorView extends BaseView<DyeColor> {

    public String id;
    public String name;
    public Color color;


    public DyeColorView(DyeColor value) {
        super(value);

        this.id = value.getId();
        this.name = value.getTranslation().get();
        this.color = value.getColor();
    }
}
