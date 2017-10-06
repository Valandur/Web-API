package valandur.webapi.json.view.misc;

import com.flowpowered.math.vector.Vector3d;
import valandur.webapi.api.json.BaseView;

public class Vector3dView extends BaseView<Vector3d> {

    public double x;
    public double y;
    public double z;


    public Vector3dView(Vector3d value) {
        super(value);

        this.x = value.getX();
        this.y = value.getY();
        this.z = value.getZ();
    }
}
