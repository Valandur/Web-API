package valandur.webapi.integration.nations;

import com.arckenver.nations.object.Rect;
import com.arckenver.nations.object.Zone;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.player.ICachedPlayer;

import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CachedZone extends CachedObject<Zone> {

    private UUID uuid;
    private String name;
    private Rect rect;

    private ICachedPlayer owner;
    private List<ICachedPlayer> coOwners;

    private Hashtable<String, Boolean> flags;
    private boolean forSale;
    private BigDecimal price;


    public CachedZone(Zone value) {
        super(value);

        this.uuid = UUID.fromString(value.getUUID().toString());
        this.name = value.getName();
        this.rect = value.getRect();

        this.owner = cacheService.getPlayer(value.getOwner()).orElse(null);
        this.coOwners = value.getCoowners().stream()
                .map(uuid -> cacheService.getPlayer(uuid).orElse(null))
                .collect(Collectors.toList());

        this.flags = new Hashtable<>(value.getFlags());
        this.forSale = value.isForSale();
        this.price = new BigDecimal(value.getPrice().toString());
    }
}
