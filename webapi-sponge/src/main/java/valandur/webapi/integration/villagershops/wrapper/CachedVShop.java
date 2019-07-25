package valandur.webapi.integration.villagershops.wrapper;

import de.dosmike.sponge.vshop.InvPrep;
import de.dosmike.sponge.vshop.NPCguard;
import de.dosmike.sponge.vshop.VillagerShops;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.Constants;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.cache.world.CachedLocation;
import valandur.webapi.serialize.JsonDetails;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApiModel("VillagerShopsShop")
public class CachedVShop extends CachedObject<NPCguard> {

    String uid;

    @ApiModelProperty(value = "The unique shop identifier; this is not the mob uuid")
    public UUID getUID() {
        return UUID.fromString(uid);
    }

    String name;

    @JsonDetails
    @ApiModelProperty(value = "The escaped shop name")
    public String getName() {
        return name;
    }

    CachedLocation location;

    @JsonDetails
    @ApiModelProperty(value = "Where the shop is currently located")
    public CachedLocation getLocation() {
        return location;
    }

    Double rotation;

    @JsonDetails
    @ApiModelProperty(value = "The mobs roations around their up-axis")
    public Double getRotation() {
        return rotation;
    }

    CachedCatalogType<EntityType> entityType;

    @JsonDetails
    @ApiModelProperty(value = "The minecraft entity type string for this shops visual entity", required = true,
            example = "minecraft:villager")
    public CachedCatalogType<EntityType> getEntityType() {
        return entityType;
    }

    String entityVariant;

    @JsonDetails
    @ApiModelProperty(value = "A very dynamic variant string for vanilla mobs, most variants as in the minecraft wiki should be supported",
            example = "butcher")
    public String getEntityVariant() {
        return entityVariant;
    }

    UUID owner;

    @JsonDetails
    @ApiModelProperty(value = "If this shop is a player shop this conatins the UUID of this shops owner. " +
            "Omitting this field or setting it to null will remove the player-shop association.")
    public UUID getOwner() {
        return owner;
    }
//	Boolean isPlayerShop;
//	@JsonDetails
//	@ApiModelProperty(value = "Returns wether this is a player shop or not", required = true)
//	public Boolean isPlayerShop() {
//		return isPlayerShop;
//	}

    List<CachedStockItem> stockItems;

    @JsonDetails
    @ApiModelProperty(value = "Returns a list of all stock items currently listed. This property is read only.")
    public List<CachedStockItem> getStockItems() {
        return stockItems;
    }

    CachedLocation stockContainer;

    @JsonDetails
    @ApiModelProperty(value = "Location where a container should reside for stocking items. " +
            "Omitting this field or setting it to null will remove the stock container. " +
            "Having a player-shop without container is undefined behaviour!")
    public CachedLocation getStockContainer() {
        return stockContainer;
    }

    public CachedVShop() {
        super(null);
    }

    public CachedVShop(NPCguard shop) {
        super(shop);

        this.uid = shop.getIdentifier().toString();
        this.name = TextSerializers.FORMATTING_CODE.serialize(shop.getDisplayName());
        this.location = new CachedLocation(shop.getLoc());
        this.rotation = shop.getRot().getY();
        this.entityType = new CachedCatalogType<>(shop.getNpcType());
        this.entityVariant = shop.getVariantName();
        this.owner = shop.getShopOwner().orElse(null);
//		this.isPlayerShop = shop.getShopOwner().isPresent();

        Optional<Location<World>> stock = shop.getStockContainer();
        this.stockContainer = stock.map(CachedLocation::new).orElse(null);

        InvPrep inv = shop.getPreparator();
        this.stockItems = new LinkedList<>();
        int s = inv.size();
        for (int i = 0; i < s; i++)
            stockItems.add(new CachedStockItem(inv.getItem(i), i, getUID()));
    }

    @Override
    public Optional<NPCguard> getLive() {
        return VillagerShops.getNPCfromShopUUID(getUID());
    }

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/vshop/shop/" + uid;
    }
}
