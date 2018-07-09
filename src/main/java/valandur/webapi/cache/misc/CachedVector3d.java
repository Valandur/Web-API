package valandur.webapi.cache.misc;

import com.flowpowered.math.vector.Vector3d;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.cache.CachedObject;

@ApiModel("Vector3d")
public class CachedVector3d extends CachedObject<Vector3d> {

    @ApiModelProperty(value = "The x-coordinate", required = true)
    public double x;

    @ApiModelProperty(value = "The y-coordinate", required = true)
    public double y;

    @ApiModelProperty(value = "The z-coordinate", required = true)
    public double z;


    public CachedVector3d(double x, double y, double z) {
        super(null);

        this.x = x;
        this.y = y;
        this.z = z;
    }
    public CachedVector3d(Vector3d value) {
        super(value);

        this.x = value.getX();
        this.y = value.getY();
        this.z = value.getZ();
    }

    @Override
    public Vector3d getLive() {
        return new Vector3d(x, y, z);
    }
}
