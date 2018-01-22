package valandur.webapi.integration.redprotect;

import br.net.fabiozumbi12.RedProtect.Sponge.Region;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flowpowered.math.vector.Vector3d;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.cache.world.CachedLocation;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.serialize.JsonDetails;

import java.util.*;

@JsonDeserialize
public class CachedRegion extends CachedObject<Region> {

    @JsonDeserialize
    private String id;
    public String getId() {
        return id;
    }

    @JsonDeserialize
    private String name;
    public String getName() {
        return name;
    }

    @JsonDeserialize
    private ICachedWorld world;
    @JsonDetails(value = false, simple = true)
    public ICachedWorld getWorld() {
        return world;
    }

    @JsonDeserialize
    private Vector3d min;
    public Vector3d getMin() {
        return min;
    }

    @JsonDeserialize
    private Vector3d max;
    public Vector3d getMax() {
        return max;
    }

    @JsonDeserialize
    private Integer priority;
    @JsonDetails
    public Integer getPriority() {
        return priority;
    }

    @JsonDeserialize
    private Boolean canDelete;
    @JsonDetails
    public Boolean getCanDelete() {
        return canDelete;
    }

    @JsonDeserialize
    private List<ICachedPlayer> leaders;
    @JsonDetails(simple = true)
    public List<ICachedPlayer> getLeaders() {
        return leaders;
    }

    @JsonDeserialize
    private List<ICachedPlayer> admins;
    @JsonDetails(simple = true)
    public List<ICachedPlayer> getAdmins() {
        return admins;
    }

    @JsonDeserialize
    private List<ICachedPlayer> members;
    @JsonDetails(simple = true)
    public List<ICachedPlayer> getMembers() {
        return members;
    }

    @JsonDeserialize
    private String welcomeMessage;
    @JsonDetails
    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    @JsonDeserialize
    private String date;
    @JsonDetails
    public String getDate() {
        return date;
    }

    @JsonDeserialize
    private HashMap<String, Object> flags;
    @JsonDetails
    public HashMap<String, Object> getFlags() {
        return flags;
    }

    @JsonDeserialize
    private CachedLocation tpPoint;
    @JsonDetails
    public CachedLocation getTpPoint() {
        return tpPoint;
    }


    public CachedRegion() {
        super(null);
    }
    public CachedRegion(Region region) {
        super(region);

        this.id = region.getID();
        this.name = region.getName();
        this.world = cacheService.getWorld(region.getWorld()).orElse(null);
        this.min = new Vector3d(region.getMinMbrX(), region.getMinY(), region.getMinMbrZ());
        this.max = new Vector3d(region.getMaxMbrX(), region.getMaxY(), region.getMaxMbrZ());
        this.priority = region.getPrior();
        this.welcomeMessage = region.getWelcome();
        this.date = region.getDate();
        this.canDelete = region.canDelete();
        this.tpPoint = region.getTPPoint() != null ? new CachedLocation(region.getTPPoint()) : null;

        this.leaders = new ArrayList<>();
        for (String uuid : region.getLeaders()) {
            Optional<ICachedPlayer> optPlayer = cacheService.getPlayer(uuid);
            optPlayer.ifPresent(player -> this.leaders.add(player));
        }

        this.admins = new ArrayList<>();
        for (String uuid : region.getAdmins()) {
            Optional<ICachedPlayer> optPlayer = cacheService.getPlayer(uuid);
            optPlayer.ifPresent(player -> this.admins.add(player));
        }

        this.members = new ArrayList<>();
        for (String uuid : region.getLeaders()) {
            Optional<ICachedPlayer> optPlayer = cacheService.getPlayer(uuid);
            optPlayer.ifPresent(player -> this.members.add(player));
        }

        this.flags = new HashMap<>(region.flags.size());
        for (Map.Entry<String, Object> entry : region.flags.entrySet()) {
            this.flags.put(entry.getKey(), cacheService.asCachedObject(entry.getValue()));
        }
    }
}
