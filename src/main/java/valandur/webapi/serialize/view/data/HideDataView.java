package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.item.HideData;
import valandur.webapi.serialize.BaseView;

@ApiModel("HideData")
public class HideDataView extends BaseView<HideData> {

    @ApiModelProperty("Gets the 'attributes hidden' state of the item stack")
    public boolean hideAttributes;

    @ApiModelProperty("Gets the 'can destory hidden' state of the item stack")
    public boolean hideCanDestroy;

    @ApiModelProperty("Gets the 'can place hidden' state of the item stack")
    public boolean hideCanPlace;

    @ApiModelProperty("Gets the 'enchantments hidden' state of the item stack")
    public boolean hideEnchantments;

    @ApiModelProperty("Gets the 'miscellaneous hidden' state of the item stack")
    public boolean hideMiscellaneous;

    @ApiModelProperty("Gets the 'unbreakable hidden' state of the item stack")
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
