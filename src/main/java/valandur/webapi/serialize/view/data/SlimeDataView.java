package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.SlimeData;
import valandur.webapi.serialize.BaseView;

@ApiModel("SlimeData")
public class SlimeDataView extends BaseView<SlimeData> {

    @ApiModelProperty(value = "The size of the slime entity", required = true)
    public int size;


    public SlimeDataView(SlimeData value) {
        super(value);

        this.size = value.size().get();
    }
}
