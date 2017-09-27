package valandur.webapi.integration.redprotect;

import br.net.fabiozumbi12.redprotect.Region;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.cache.CacheService;

import java.util.*;

public class CachedRegion {

    private String id;
    public String getId() {
        return id;
    }

    private String name;
    public String getName() {
        return name;
    }

    private int priority;
    public int getPrior() {
        return priority;
    }

    private ICachedWorld world;
    public ICachedWorld getWorld() {
        return world;
    }

    private List<ICachedPlayer> leaders;
    public List<ICachedPlayer> getLeaders() {
        return leaders;
    }

    private List<ICachedPlayer> admins;
    public List<ICachedPlayer> getAdmins() {
        return admins;
    }

    private List<ICachedPlayer> members;
    public List<ICachedPlayer> getMembers() {
        return members;
    }

    private String wMessage;
    public String getwMessage() {
        return wMessage;
    }

    private String date;
    public String getDate() {
        return date;
    }

    private Map<String, Object> flags;
    public Map<String, Object> getFlags() {
        return flags;
    }

    private Location<World> tpPoint;
    public Location<World> getTppoint() {
        return tpPoint;
    }


    public CachedRegion(Region region) {
        CacheService cache = WebAPI.getCacheService();

        this.id = region.getID();
        this.name = region.getName();
        this.world = WebAPI.getCacheService().getWorld(region.getWorld()).orElse(null);
        this.priority = region.getPrior();
        this.leaders = new ArrayList<>();
        for (String uuid : region.getLeaders()) {
            Optional<ICachedPlayer> optPlayer = cache.getPlayer(uuid);
            optPlayer.ifPresent(player -> this.leaders.add(player));
        }
        this.admins = new ArrayList<>();
        for (String uuid : region.getAdmins()) {
            Optional<ICachedPlayer> optPlayer = cache.getPlayer(uuid);
            optPlayer.ifPresent(player -> this.admins.add(player));
        }
        this.members = new ArrayList<>();
        for (String uuid : region.getLeaders()) {
            Optional<ICachedPlayer> optPlayer = cache.getPlayer(uuid);
            optPlayer.ifPresent(player -> this.members.add(player));
        }
        this.wMessage = region.getWelcome();
        this.date = region.getDate();
        this.flags = new HashMap<>(region.flags);
        this.tpPoint = region.getTPPoint() != null ? region.getTPPoint().copy() : null;
    }
}
