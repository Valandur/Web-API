package valandur.webapi.cache.misc;

import com.flowpowered.math.vector.Vector3i;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.cache.CachedObject;

@ApiModel("Vector3i")
public class CachedVector3i extends CachedObject<Vector3i> {

    @ApiModelProperty(value = "The x-coordinate", required = true)
    public int x;

    @ApiModelProperty(value = "The y-coordinate", required = true)
    public int y;

    @ApiModelProperty(value = "The z-coordinate", required = true)
    public int z;


    public CachedVector3i(Vector3i value) {
        super(value);

        this.x = value.getX();
        this.y = value.getY();
        this.z = value.getZ();
    }

    @Override
    public Vector3i getLive() {
        return new Vector3i(x, y, z);
    }
}
