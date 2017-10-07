package valandur.webapi.integration.redprotect;

import br.net.fabiozumbi12.redprotect.Region;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.cache.world.CachedLocation;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.json.JsonDetails;

import java.util.*;

public class CachedRegion extends CachedObject<Region> {

    private String id;
    public String getId() {
        return id;
    }

    private String name;
    public String getName() {
        return name;
    }

    private int priority;
    @JsonDetails
    public int getPrior() {
        return priority;
    }

    private ICachedWorld world;
    @JsonDetails(value = false, simple = true)
    public ICachedWorld getWorld() {
        return world;
    }

    private List<ICachedPlayer> leaders;
    @JsonDetails(simple = true)
    public List<ICachedPlayer> getLeaders() {
        return leaders;
    }

    private List<ICachedPlayer> admins;
    @JsonDetails(simple = true)
    public List<ICachedPlayer> getAdmins() {
        return admins;
    }

    private List<ICachedPlayer> members;
    @JsonDetails(simple = true)
    public List<ICachedPlayer> getMembers() {
        return members;
    }

    private String wMessage;
    @JsonDetails
    public String getwMessage() {
        return wMessage;
    }

    private String date;
    @JsonDetails
    public String getDate() {
        return date;
    }

    private Map<String, Object> flags;
    @JsonDetails
    public Map<String, Object> getFlags() {
        return flags;
    }

    private CachedLocation tpPoint;
    @JsonDetails
    public CachedLocation getTppoint() {
        return tpPoint;
    }


    public CachedRegion(Region region) {
        super(region);

        this.id = region.getID();
        this.name = region.getName();
        this.world = cacheService.getWorld(region.getWorld()).orElse(null);
        this.priority = region.getPrior();
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
        this.wMessage = region.getWelcome();
        this.date = region.getDate();
        this.flags = new HashMap<>(region.flags.size());
        for (Map.Entry<String, Object> entry : region.flags.entrySet()) {
            this.flags.put(entry.getKey(), cacheService.asCachedObject(entry.getValue()));
        }
        this.tpPoint = region.getTPPoint() != null ? new CachedLocation(region.getTPPoint()) : null;
    }
}
