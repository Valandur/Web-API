package valandur.webapi.serialize.view.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.Profession;
import valandur.webapi.serialize.BaseView;

@ApiModel("Career")
public class CareerView extends BaseView<Career> {

    @ApiModelProperty(value = "The unique id of the career", required = true)
    public String id;

    @ApiModelProperty(value = "The name of the career", required = true)
    public String name;

    @ApiModelProperty(value = "The profession within the career", required = true)
    public Profession profession;


    public CareerView(Career value) {
        super(value);

        this.id = value.getId();
        this.name = value.getTranslation().get();
        this.profession = value.getProfession();
    }
}
