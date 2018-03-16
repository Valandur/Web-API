package valandur.webapi.serialize.param;

import com.flowpowered.math.vector.Vector3i;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ext.ParamConverter;

public class Vector3iParamConverter implements ParamConverter<Vector3i> {

    @Override
    public Vector3i fromString(String value) {
        // If we didn't specify a vector3i don't try to parse one
        if (value == null)
            return null;

        String[] splits = value.split("\\|");
        if (splits.length < 3) {
            throw new BadRequestException("Invalid Vector3i");
        }

        try {
            int x = Integer.parseInt(splits[0]);
            int y = Integer.parseInt(splits[1]);
            int z = Integer.parseInt(splits[2]);
            return new Vector3i(x, y, z);
        } catch (NumberFormatException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public String toString(Vector3i value) {
        return value.getX() + "|" + value.getY() + "|" + value.getZ();
    }
}
