package valandur.webapi.serialize.param;

import com.flowpowered.math.vector.Vector3d;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ext.ParamConverter;

public class Vector3dParamConverter implements ParamConverter<Vector3d> {

    @Override
    public Vector3d fromString(String value) {
        // If we didn't specify a vector3d don't try to parse one
        if (value == null)
            return null;

        String[] splits = value.split("\\|");
        if (splits.length < 3) {
            throw new BadRequestException("Invalid Vector3d");
        }

        try {
            double x = Double.parseDouble(splits[0]);
            double y = Double.parseDouble(splits[1]);
            double z = Double.parseDouble(splits[2]);
            return new Vector3d(x, y, z);
        } catch (NumberFormatException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public String toString(Vector3d value) {
        return value.getX() + "|" + value.getY() + "|" + value.getZ();
    }
}
