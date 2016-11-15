package valandur.webapi.misc;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.gson.JsonObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    public static String getOptStringOrNull(Optional<String> object) {
        if (object.isPresent())
            return object.get();
        return null;
    }
    public static String getOptUUIDOrNull(Optional<UUID> object) {
        if (object.isPresent())
            return object.get().toString();
        return null;
    }

    public static String lowerFirst(String text) {
        return Character.toLowerCase(text.charAt(0)) + text.substring(1);
    }

    public static Optional<World> getWorldFromString(String worldNameOrUUID) {
        Optional<World> world = Sponge.getServer().getWorld(worldNameOrUUID);
        if (world.isPresent()) return world;
        if (worldNameOrUUID.split("-").length != 5) return Optional.empty();
        return Sponge.getServer().getWorld(UUID.fromString(worldNameOrUUID));
    }

    public static Optional<Player> getPlayerFromString(String playerNameOrUUID) {
        Optional<Player> player = Sponge.getServer().getPlayer(playerNameOrUUID);
        if (player.isPresent()) return player;
        if (playerNameOrUUID.split("-").length != 5) return Optional.empty();
        return Sponge.getServer().getPlayer(UUID.fromString(playerNameOrUUID));
    }
}
