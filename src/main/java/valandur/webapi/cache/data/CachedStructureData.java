package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.tileentity.StructureData;
import org.spongepowered.api.data.type.StructureMode;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.cache.misc.CachedVector3i;

@ApiModel("StructureData")
public class CachedStructureData extends CachedObject<StructureData> {

    @ApiModelProperty(value = "The author of this structure", required = true)
    public String author;

    @ApiModelProperty(value = "True if entities are not part of this structure, false otherwise", required = true)
    public boolean ignoreEntities;

    @ApiModelProperty(value = "The integrity of the structure", required = true)
    public float integrity;

    @ApiModelProperty(value = "The mode of the structure", required = true)
    public CachedCatalogType<StructureMode> mode;

    @ApiModelProperty(value = "The position of the structure", required = true)
    public CachedVector3i position;

    @ApiModelProperty(value = "True if the structure is powered, false otherwise", required = true)
    public boolean powered;

    @ApiModelProperty(value = "The seed of this structure", required = true)
    public long seed;

    @ApiModelProperty(value = "True if the air blocks for this structure are shown, false otherwise", required = true)
    public boolean showAir;

    @ApiModelProperty(value = "True if the bounding box for this structure is shown, false otherwise", required = true)
    public boolean showBoundingBox;

    @ApiModelProperty(value = "The size of this structure", required = true)
    public CachedVector3i size;


    public CachedStructureData(StructureData value) {
        super(value);

        this.author = value.author().get();
        this.ignoreEntities = value.ignoreEntities().get();
        this.integrity = value.integrity().get();
        this.mode = new CachedCatalogType<>(value.mode().get());
        this.position = new CachedVector3i(value.position().get());
        this.powered = value.powered().get();
        this.seed = value.seed().get();
        this.showAir = value.showAir().get();
        this.showBoundingBox = value.showBoundingBox().get();
        this.size = new CachedVector3i(value.size().get());
    }
}
