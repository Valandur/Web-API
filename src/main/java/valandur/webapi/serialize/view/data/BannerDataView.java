package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.tileentity.BannerData;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.type.DyeColor;
import valandur.webapi.api.serialize.BaseView;

import java.util.List;

public class BannerDataView extends BaseView<BannerData> {

    public DyeColor color;
    public List<PatternLayer> patterns;


    public BannerDataView(BannerData value) {
        super(value);

        this.color = value.baseColor().get();
        this.patterns = value.patternsList().getAll();
    }
}
