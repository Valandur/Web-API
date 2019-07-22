package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.ArmorStandData;
import valandur.webapi.serialize.BaseView;

@ApiModel("ArmorStandData")
public class ArmorStandDataView extends BaseView<ArmorStandData> {

    @ApiModelProperty(value = "True if the armor stand has arms, false otherwise", required = true)
    public boolean arms;

    @ApiModelProperty(value = "True if the armor stand has a base plate, false otherwise", required = true)
    public boolean basePlate;

    @ApiModelProperty(value = "True if the armor stand has a marker, false otherwise", required = true)
    public boolean marker;

    @ApiModelProperty(value = "True if the armor stand is small, false otherwise", required = true)
    public boolean small;


    public ArmorStandDataView(ArmorStandData value) {
        super(value);

        this.arms = value.arms().get();
        this.basePlate = value.basePlate().get();
        this.marker = value.marker().get();
        this.small = value.small().get();
    }
}
