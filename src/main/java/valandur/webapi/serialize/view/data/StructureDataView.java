/*package valandur.webapi.serialize.view.data;

import com.flowpowered.math.vector.Vector3i;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.tileentity.StructureData;
import org.spongepowered.api.data.type.StructureMode;
import valandur.webapi.api.serialize.BaseView;

@ApiModel("StructureData")
public class StructureDataView extends BaseView<StructureData> {

    @ApiModelProperty(value = "The author of this structure", required = true)
    public String author;

    @ApiModelProperty(value = "True if entities are not part of this structure, false otherwise", required = true)
    public boolean ignoreEntities;

    @ApiModelProperty(value = "The integrity of the structure", required = true)
    public float integrity;

    @ApiModelProperty(value = "The mode of the structure", required = true)
    public StructureMode mode;

    @ApiModelProperty(value = "The position of the structure", required = true)
    public Vector3i position;

    @ApiModelProperty(value = "True if the structure is powered, false otherwise", required = true)
    public boolean powered;

    @ApiModelProperty(value = "The seed of this structure", required = true)
    public long seed;

    @ApiModelProperty(value = "True if the air blocks for this structure are shown, false otherwise", required = true)
    public boolean showAir;

    @ApiModelProperty(value = "True if the bounding box for this structure is shown, false otherwise", required = true)
    public boolean showBoundingBox;

    @ApiModelProperty(value = "The size of this structure", required = true)
    public Vector3i size;


    public StructureDataView(StructureData value) {
        super(value);

        this.author = value.author().get();
        this.ignoreEntities = value.ignoreEntities().get();
        this.integrity = value.integrity().get();
        this.mode = value.mode().get();
        this.position = value.position().get();
        this.powered = value.powered().get();
        this.seed = value.seed().get();
        this.showAir = value.showAir().get();
        this.showBoundingBox = value.showBoundingBox().get();
        this.size = value.size().get();
    }
}
*/