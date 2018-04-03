package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.AgeableData;
import valandur.webapi.api.serialize.BaseView;

@ApiModel("AgeableData")
public class AgeableDataView extends BaseView<AgeableData> {

    @ApiModelProperty(value = "True if this entity is an adult, false otherwise", required = true)
    public boolean adult;

    @ApiModelProperty(value = "The age of the entity", required = true)
    public int age;

    public AgeableDataView(AgeableData value) {
        super(value);

        this.adult = value.adult().get();
        this.age = value.age().get();
    }
}
