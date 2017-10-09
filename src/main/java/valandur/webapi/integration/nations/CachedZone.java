package valandur.webapi.integration.nations;

import com.arckenver.nations.object.Zone;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.player.ICachedPlayer;

import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CachedZone extends CachedObject<Zone> {

    private UUID uuid;
    public UUID getUuid() {
        return uuid;
    }

    private String name;
    public String getName() {
        return name;
    }

    private CachedRect rect;
    public CachedRect getRect() {
        return rect;
    }

    private ICachedPlayer owner;
    public ICachedPlayer getOwner() {
        return owner;
    }

    private List<ICachedPlayer> coOwners;
    public List<ICachedPlayer> getCoOwners() {
        return coOwners;
    }

    private Hashtable<String, Boolean> flags;
    public Hashtable<String, Boolean> getFlags() {
        return flags;
    }

    private boolean forSale;
    public boolean isForSale() {
        return forSale;
    }

    private String price;
    public String getPrice() {
        return price;
    }


    public CachedZone(Zone value) {
        super(value);

        this.uuid = UUID.fromString(value.getUUID().toString());
        this.name = value.getName();
        this.rect = new CachedRect(value.getRect());

        this.owner = cacheService.getPlayer(value.getOwner()).orElse(null);
        this.coOwners = value.getCoowners().stream()
                .map(uuid -> cacheService.getPlayer(uuid).orElse(null))
                .collect(Collectors.toList());

        this.flags = new Hashtable<>(value.getFlags());
        this.forSale = value.isForSale();
        this.price = value.getPrice().toString();
    }
}
