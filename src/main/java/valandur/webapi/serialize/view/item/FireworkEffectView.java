package valandur.webapi.serialize.view.item;

import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShape;
import org.spongepowered.api.util.Color;
import valandur.webapi.api.serialize.BaseView;

import java.util.List;

public class FireworkEffectView extends BaseView<FireworkEffect> {

    public FireworkShape shape;
    public boolean flickers;
    public List<Color> colors;
    public List<Color> fadeColors;
    public boolean trail;


    public FireworkEffectView(FireworkEffect value) {
        super(value);

        this.shape = value.getShape();
        this.flickers = value.flickers();
        this.colors = value.getColors();
        this.fadeColors = value.getFadeColors();
        this.trail = value.hasTrail();
    }
}
