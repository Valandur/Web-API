package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;
import valandur.webapi.serialize.BaseView;

@ApiModel("DurabilityData")
public class DurabilityDataView extends BaseView<DurabilityData> {

    @ApiModelProperty(value = "True if this entity is unbreakable, false otherwise", required = true)
    public boolean unbreakable;

    @ApiModelProperty(value = "The remaining durability of this entity", required = true)
    public int durability;


    public DurabilityDataView(DurabilityData value) {
        super(value);

        this.unbreakable = value.unbreakable().get();
        this.durability = value.durability().get();
    }
}
