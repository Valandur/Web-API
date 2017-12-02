package valandur.webapi.serialize.view.data;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.data.manipulator.mutable.tileentity.EndGatewayData;
import valandur.webapi.api.serialize.BaseView;

public class EndGatewayDataView extends BaseView<EndGatewayData> {

    public long age;
    public boolean exactTeleport;
    public Vector3i exitPosition;
    public int teleportCooldown;


    public EndGatewayDataView(EndGatewayData value) {
        super(value);

        this.age = value.age().get();
        this.exactTeleport = value.exactTeleport().get();
        this.exitPosition = value.exitPosition().get();
        this.teleportCooldown = value.teleportCooldown().get();
    }
}
