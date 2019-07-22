package valandur.webapi.serialize.view.misc;

import com.flowpowered.math.vector.Vector3i;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.serialize.BaseView;

@ApiModel("Vector3i")
public class Vector3iView extends BaseView<Vector3i> {

    @ApiModelProperty(value = "The x-coordinate", required = true)
    public int x;

    @ApiModelProperty(value = "The y-coordinate", required = true)
    public int y;

    @ApiModelProperty(value = "The z-coordinate", required = true)
    public int z;


    public Vector3iView(Vector3i value) {
        super(value);

        this.x = value.getX();
        this.y = value.getY();
        this.z = value.getZ();
    }
}
