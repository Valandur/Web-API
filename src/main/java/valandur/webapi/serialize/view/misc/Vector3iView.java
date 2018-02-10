package valandur.webapi.serialize.view.misc;

import com.flowpowered.math.vector.Vector3i;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.api.serialize.BaseView;

@ApiModel("Vector3i")
public class Vector3iView extends BaseView<Vector3i> {

    @ApiModelProperty("The x-coordinate")
    public int x;

    @ApiModelProperty("The y-coordinate")
    public int y;

    @ApiModelProperty("The z-coordinate")
    public int z;


    public Vector3iView(Vector3i value) {
        super(value);

        this.x = value.getX();
        this.y = value.getY();
        this.z = value.getZ();
    }
}
