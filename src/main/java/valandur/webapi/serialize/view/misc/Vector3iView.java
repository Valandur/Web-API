package valandur.webapi.serialize.view.misc;

import com.flowpowered.math.vector.Vector3i;
import valandur.webapi.api.serialize.BaseView;

public class Vector3iView extends BaseView<Vector3i> {

    public int x;
    public int y;
    public int z;


    public Vector3iView(Vector3i value) {
        super(value);

        this.x = value.getX();
        this.y = value.getY();
        this.z = value.getZ();
    }
}
