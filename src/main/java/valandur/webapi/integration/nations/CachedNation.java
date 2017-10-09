package valandur.webapi.integration.nations;

import com.arckenver.nations.object.Nation;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.cache.world.CachedLocation;
import valandur.webapi.api.json.JsonDetails;

import java.util.*;
import java.util.stream.Collectors;

public class CachedNation extends CachedObject<Nation> {

    private UUID uuid;
    public UUID getUuid() {
        return uuid;
    }

    private String name;
    public String getName() {
        return name;
    }

    private String tag;
    public String getTag() {
        return tag;
    }

    private ICachedPlayer president;
    public ICachedPlayer getPresident() {
        return president;
    }

    private String realName;
    @JsonDetails
    public String getRealName() {
        return realName;
    }

    private double upkeep;
    @JsonDetails
    public double getUpkeep() {
        return upkeep;
    }

    private double taxes;
    @JsonDetails
    public double getTaxes() {
        return taxes;
    }

    private Hashtable<String, Boolean> flags;
    @JsonDetails
    public Hashtable<String, Boolean> getFlags() {
        return flags;
    }

    private List<ICachedPlayer> citizens;
    @JsonDetails
    public List<ICachedPlayer> getCitizens() {
        return citizens;
    }

    private List<ICachedPlayer> ministers;
    @JsonDetails
    public List<ICachedPlayer> getMinisters() {
        return ministers;
    }

    private List<ICachedPlayer> staff;
    @JsonDetails
    public List<ICachedPlayer> getStaff() {
        return staff;
    }

    private Hashtable<String, CachedLocation> spawns;
    @JsonDetails
    public Hashtable<String, CachedLocation> getSpawns() {
        return spawns;
    }

    private Collection<CachedRect> rects;
    @JsonDetails
    public Collection<CachedRect> getRects() {
        return rects;
    }

    private Collection<CachedZone> zones;
    @JsonDetails
    public Collection<CachedZone> getZones() {
        return zones;
    }


    public CachedNation(Nation value) {
        super(value);

        this.uuid = value.getUUID();
        this.name = value.getName();
        this.tag = value.getTag();
        this.president = cacheService.getPlayer(value.getPresident()).orElse(null);

        this.realName = value.getRealName();
        this.upkeep = value.getUpkeep();
        this.taxes = value.getTaxes();
        this.flags = value.getFlags();

        this.citizens = value.getCitizens().stream()
                .map(uuid -> cacheService.getPlayer(uuid).orElse(null))
                .collect(Collectors.toList());

        this.ministers = value.getMinisters().stream()
                .map(uuid -> cacheService.getPlayer(uuid).orElse(null))
                .collect(Collectors.toList());

        this.staff = value.getStaff().stream()
                .map(uuid -> cacheService.getPlayer(uuid).orElse(null))
                .collect(Collectors.toList());

        this.spawns = new Hashtable<>();
        for (Map.Entry<String, Location<World>> entry : value.getSpawns().entrySet()) {
            this.spawns.put(entry.getKey(), new CachedLocation(entry.getValue()));
        }
        this.rects = value.getRegion().getRects().stream().map(CachedRect::new).collect(Collectors.toList());
        this.zones = value.getZones().values().stream().map(CachedZone::new).collect(Collectors.toList());
    }
}
