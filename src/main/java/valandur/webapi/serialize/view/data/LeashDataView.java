package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.LeashData;
import org.spongepowered.api.entity.Entity;
import valandur.webapi.api.serialize.BaseView;

@ApiModel("LeashData")
public class LeashDataView extends BaseView<LeashData> {

    @ApiModelProperty(value = "The holder of this entity's leash", required = true)
    public Entity holder;


    public LeashDataView(LeashData value) {
        super(value);

        this.holder = value.leashHolder().get();
    }
}
