package valandur.webapi.serialize.view.misc;

import com.flowpowered.math.vector.Vector3d;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.serialize.BaseView;

@ApiModel("Vector3d")
public class Vector3dView extends BaseView<Vector3d> {

    @ApiModelProperty(value = "The x-coordinate", required = true)
    public double x;

    @ApiModelProperty(value = "The y-coordinate", required = true)
    public double y;

    @ApiModelProperty(value = "The z-coordinate", required = true)
    public double z;


    public Vector3dView(Vector3d value) {
        super(value);

        this.x = value.getX();
        this.y = value.getY();
        this.z = value.getZ();
    }
}
