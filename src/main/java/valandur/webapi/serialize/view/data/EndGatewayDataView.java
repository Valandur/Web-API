package valandur.webapi.serialize.view.data;

import com.flowpowered.math.vector.Vector3i;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.tileentity.EndGatewayData;
import valandur.webapi.api.serialize.BaseView;

@ApiModel("EndGatewayData")
public class EndGatewayDataView extends BaseView<EndGatewayData> {

    @ApiModelProperty(value = "The age of this gateway", required = true)
    public long age;

    @ApiModelProperty(value = "True if this is an exact teleport, false otherwise", required = true)
    public boolean exactTeleport;

    @ApiModelProperty(value = "The exit position in the nether", required = true)
    public Vector3i exitPosition;

    @ApiModelProperty(value = "The cooldown of the teleport", required = true)
    public int teleportCooldown;


    public EndGatewayDataView(EndGatewayData value) {
        super(value);

        this.age = value.age().get();
        this.exactTeleport = value.exactTeleport().get();
        this.exitPosition = value.exitPosition().get();
        this.teleportCooldown = value.teleportCooldown().get();
    }
}
