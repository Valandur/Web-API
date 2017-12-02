package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.item.HideData;
import valandur.webapi.api.serialize.BaseView;

public class HideDataView extends BaseView<HideData> {

    public boolean hideAttributes;
    public boolean hideCanDestroy;
    public boolean hideCanPlace;
    public boolean hideEnchantments;
    public boolean hideMiscellaneous;
    public boolean hideUnbreakable;


    public HideDataView(HideData value) {
        super(value);

        this.hideAttributes = value.hideAttributes().get();
        this.hideCanDestroy = value.hideCanDestroy().get();
        this.hideCanPlace = value.hideCanPlace().get();
        this.hideEnchantments = value.hideEnchantments().get();
        this.hideMiscellaneous = value.hideMiscellaneous().get();
        this.hideUnbreakable = value.hideUnbreakable().get();
    }
}
