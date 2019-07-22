package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.HorseData;
import org.spongepowered.api.data.type.HorseColor;
import org.spongepowered.api.data.type.HorseStyle;
import valandur.webapi.serialize.BaseView;

@ApiModel("HorseData")
public class HorseDataView extends BaseView<HorseData> {

    @ApiModelProperty(value = "The color of the horse", required = true)
    public HorseColor color;

    @ApiModelProperty(value = "The style of the horse", required = true)
    public HorseStyle style;


    public HorseDataView(HorseData value) {
        super(value);

        this.color = value.color().get();
        this.style = value.style().get();
    }
}
