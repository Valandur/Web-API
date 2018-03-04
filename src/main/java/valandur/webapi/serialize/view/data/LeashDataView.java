package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.LeashData;
import valandur.webapi.api.cache.entity.ICachedEntity;
import valandur.webapi.api.serialize.BaseView;
import valandur.webapi.cache.entity.CachedEntity;

@ApiModel("LeashData")
public class LeashDataView extends BaseView<LeashData> {

    @ApiModelProperty(value = "The holder of this entity's leash", required = true)
    public ICachedEntity holder;


    public LeashDataView(LeashData value) {
        super(value);

        this.holder = new CachedEntity(value.leashHolder().get());
    }
}
