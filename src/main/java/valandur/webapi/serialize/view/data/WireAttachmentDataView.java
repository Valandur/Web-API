package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.block.WireAttachmentData;
import org.spongepowered.api.data.type.WireAttachmentType;
import valandur.webapi.serialize.BaseView;

@ApiModel("WireAttachmentData")
public class WireAttachmentDataView extends BaseView<WireAttachmentData> {

    @ApiModelProperty(value = "The type of wire attachment to the east of this entity", required = true)
    public WireAttachmentType east;

    @ApiModelProperty(value = "The type of wire attachment to the north of this entity", required = true)
    public WireAttachmentType north;

    @ApiModelProperty(value = "The type of wire attachment to the south of this entity", required = true)
    public WireAttachmentType south;

    @ApiModelProperty(value = "The type of wire attachment to the west of this entity", required = true)
    public WireAttachmentType west;


    public WireAttachmentDataView(WireAttachmentData value) {
        super(value);

        this.east = value.wireAttachmentEast().get();
        this.north = value.wireAttachmentNorth().get();
        this.south = value.wireAttachmentSouth().get();
        this.west = value.wireAttachmentWest().get();
    }
}
