package valandur.webapi.serialize.view.misc;

import com.flowpowered.math.vector.Vector3d;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.api.serialize.BaseView;

@ApiModel("Vector3d")
public class Vector3dView extends BaseView<Vector3d> {

    @ApiModelProperty("The x-coordinate")
    public double x;

    @ApiModelProperty("The y-coordinate")
    public double y;

    @ApiModelProperty("The z-coordinate")
    public double z;


    public Vector3dView(Vector3d value) {
        super(value);

        this.x = value.getX();
        this.y = value.getY();
        this.z = value.getZ();
    }
}
