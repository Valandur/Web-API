package valandur.webapi.misc;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.gson.JsonObject;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Util {
    public static JsonObject positionToJson(Vector3i vector) {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", vector.getX());
        obj.addProperty("y", vector.getX());
        obj.addProperty("z", vector.getX());
        return obj;
    }

    public static JsonObject positionToJson(Vector3d vector) {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", vector.getX());
        obj.addProperty("y", vector.getY());
        obj.addProperty("z", vector.getZ());
        return obj;
    }

    public static JsonObject positionToJson(Location<World> location) {
        return positionToJson(location, true);
    }
    public static JsonObject positionToJson(Location<World> location, boolean includeWorld) {
        JsonObject obj = new JsonObject();
        if (includeWorld) obj.addProperty("world", location.getExtent().getName());
        obj.addProperty("x", location.getX());
        obj.addProperty("y", location.getY());
        obj.addProperty("z", location.getZ());
        return obj;
    }
}
