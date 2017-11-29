package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.block.WireAttachmentData;
import org.spongepowered.api.data.type.WireAttachmentType;
import valandur.webapi.api.serialize.BaseView;

public class WireAttachmentDataView extends BaseView<WireAttachmentData> {

    public WireAttachmentType east;
    public WireAttachmentType north;
    public WireAttachmentType south;
    public WireAttachmentType west;


    public WireAttachmentDataView(WireAttachmentData value) {
        super(value);

        this.east = value.wireAttachmentEast().get();
        this.north = value.wireAttachmentNorth().get();
        this.south = value.wireAttachmentSouth().get();
        this.west = value.wireAttachmentWest().get();
    }
}
