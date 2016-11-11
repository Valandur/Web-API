package valandur.webapi.misc;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.gson.JsonObject;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class Util {
    public static String[] getPathParts(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null) return new String[] { };
        return path.substring(1).split("/");
    }

    public static Map<String, String> getQueryParts(HttpServletRequest req) {
        Map<String, String> map = new HashMap<>();
        String[] splits = req.getQueryString().split("&");
        for (String split : splits) {
            String[] subSplits = split.split("=");
            map.put(subSplits[0], subSplits[1]);
        }
        return map;
    }

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
