package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.block.WireAttachmentData;
import org.spongepowered.api.data.type.WireAttachmentType;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;

@ApiModel("WireAttachmentData")
public class CachedWireAttachmentData extends CachedObject<WireAttachmentData> {

    @ApiModelProperty(value = "The type of wire attachment to the east of this entity", required = true)
    public CachedCatalogType<WireAttachmentType> east;

    @ApiModelProperty(value = "The type of wire attachment to the north of this entity", required = true)
    public CachedCatalogType<WireAttachmentType> north;

    @ApiModelProperty(value = "The type of wire attachment to the south of this entity", required = true)
    public CachedCatalogType<WireAttachmentType> south;

    @ApiModelProperty(value = "The type of wire attachment to the west of this entity", required = true)
    public CachedCatalogType<WireAttachmentType> west;


    public CachedWireAttachmentData(WireAttachmentData value) {
        super(value);

        this.east = new CachedCatalogType<>(value.wireAttachmentEast().get());
        this.north = new CachedCatalogType<>(value.wireAttachmentNorth().get());
        this.south = new CachedCatalogType<>(value.wireAttachmentSouth().get());
        this.west = new CachedCatalogType<>(value.wireAttachmentWest().get());
    }
}
