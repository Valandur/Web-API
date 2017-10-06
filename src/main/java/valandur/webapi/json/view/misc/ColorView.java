package valandur.webapi.json.view.misc;

import org.spongepowered.api.util.Color;
import valandur.webapi.api.json.BaseView;

public class ColorView extends BaseView<Color> {

    public int r;
    public int g;
    public int b;


    public ColorView(Color value) {
        super(value);

        this.r = value.getRed();
        this.g = value.getGreen();
        this.b = value.getBlue();
    }
}
