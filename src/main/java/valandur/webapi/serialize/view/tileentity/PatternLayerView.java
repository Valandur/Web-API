package valandur.webapi.serialize.view.tileentity;

import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.type.BannerPatternShape;
import org.spongepowered.api.data.type.DyeColor;
import valandur.webapi.api.serialize.BaseView;

public class PatternLayerView extends BaseView<PatternLayer> {

    public BannerPatternShape shape;
    public DyeColor color;


    public PatternLayerView(PatternLayer value) {
        super(value);

        this.shape = value.getShape();
        this.color = value.getColor();
    }
}
