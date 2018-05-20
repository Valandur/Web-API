package valandur.webapi.serialize.view.misc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.util.Color;
import valandur.webapi.serialize.BaseView;

@ApiModel("DyeColor")
public class DyeColorView extends BaseView<DyeColor> {

    @ApiModelProperty(value = "The unique id of this dye color", required = true)
    public String id;

    @ApiModelProperty(value = "The name of this dye color", required = true)
    public String name;

    @ApiModelProperty(value = "The color represented by this dye color", required = true)
    public Color color;


    public DyeColorView(DyeColor value) {
        super(value);

        this.id = value.getId();
        this.name = value.getTranslation().get();
        this.color = value.getColor();
    }
}
