package valandur.webapi.integration.nations;

import com.arckenver.nations.object.Nation;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.cache.world.CachedLocation;

import java.util.*;
import java.util.stream.Collectors;

public class CachedNation extends CachedObject<Nation> {

    private UUID uuid;
    private String name;
    private String tag;
    private ICachedPlayer president;

    private String realName;
    private double upkeep;
    private double taxes;
    private Hashtable<String, Boolean> flags;

    private List<ICachedPlayer> citizens;
    private List<ICachedPlayer> ministers;
    private List<ICachedPlayer> staff;

    private Hashtable<String, CachedLocation> spawns;
    private Collection<CachedRect> rects;
    private Collection<CachedZone> zones;

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
