package valandur.webapi.integration.redprotect;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.cache.world.CachedLocation;
import valandur.webapi.api.cache.world.ICachedWorld;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@JsonDeserialize
public class BaseRegionRequest {

    @JsonDeserialize
    private List<String> leaders;
    public List<String> getLeaders() {
        return leaders;
    }

    @JsonDeserialize
    private List<String> admins;
    public List<String> getAdmins() {
        return admins;
    }

    @JsonDeserialize
    private List<String> members;
    public List<String> getMembers() {
        return members;
    }

    @JsonDeserialize
    private Integer priority;
    public Integer getPriority() {
        return priority;
    }

    @JsonDeserialize
    private String welcomeMessage;
    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    @JsonDeserialize
    private HashMap<String, Object> flags;
    public HashMap<String, Object> getFlags() {
        return flags;
    }

    @JsonDeserialize
    private String world;
    public Optional<ICachedWorld> getWorld() {
        return WebAPIAPI.getCacheService().flatMap(srv -> srv.getWorld(world));
    }

    @JsonDeserialize
    private JsonNode tpPoint;
    public Optional<CachedLocation> getTpPos() {
        if (tpPoint == null)
            return Optional.empty();

        String worldString = tpPoint.get("world").asText();
        Optional<ICachedWorld> optWorld = WebAPIAPI.getCacheService().flatMap(srv -> srv.getWorld(worldString));
        if (!optWorld.isPresent())
            return Optional.empty();

        double x = tpPoint.get("x").asDouble();
        double y = tpPoint.get("y").asDouble();
        double z = tpPoint.get("z").asDouble();

        return Optional.of(new CachedLocation(optWorld.get(), x, y, z));
    }

    @JsonDeserialize
    private Boolean canDelete;
    public Boolean canDelete() {
        return canDelete;
    }
}
