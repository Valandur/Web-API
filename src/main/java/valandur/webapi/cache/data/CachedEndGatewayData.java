package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.tileentity.EndGatewayData;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedVector3i;

@ApiModel("EndGatewayData")
public class CachedEndGatewayData extends CachedObject<EndGatewayData> {

    @ApiModelProperty(value = "The age of this gateway", required = true)
    public long age;

    @ApiModelProperty(value = "True if this is an exact teleport, false otherwise", required = true)
    public boolean exactTeleport;

    @ApiModelProperty(value = "The exit position in the nether", required = true)
    public CachedVector3i exitPosition;

    @ApiModelProperty(value = "The cooldown of the teleport", required = true)
    public int teleportCooldown;


    public CachedEndGatewayData(EndGatewayData value) {
        super(value);

        this.age = value.age().get();
        this.exactTeleport = value.exactTeleport().get();
        this.exitPosition = new CachedVector3i(value.exitPosition().get());
        this.teleportCooldown = value.teleportCooldown().get();
    }
}
