package valandur.webapi.integration.mmcrestrict;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.moddedminecraft.mmcrestrict.Data.ItemData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.util.Constants;

import java.util.Optional;

@ApiModel("MMCRestrictItem")
public class CachedItemData extends CachedObject<ItemData> {

    private CachedCatalogType<ItemType> item;
    @ApiModelProperty(value = "The item type that is banned", required = true)
    public CachedCatalogType<ItemType> getItem() {
        return item;
    }
    public void setItem(CachedCatalogType<ItemType> item) {
        this.item = item;
    }

    private String banReason;
    @ApiModelProperty(value = "The reason why the item is banned", required = true)
    public String getBanReason() {
        return banReason;
    }

    private Boolean ownershipBanned;
    @ApiModelProperty(value = "True if ownership of this item is banned, false otherwise", required = true)
    public Boolean getOwnershipBanned() {
        return ownershipBanned;
    }

    private Boolean usageBanned;
    @ApiModelProperty(value = "True if the usage of this item is banned, false otherwise", required = true)
    public Boolean getUsageBanned() {
        return usageBanned;
    }

    private Boolean breakingBanned;
    @ApiModelProperty(value = "True if breaking of this item is banned, false otherwise", required = true)
    public Boolean getBreakingBanned() {
        return breakingBanned;
    }

    private Boolean placingBanned;
    @ApiModelProperty(value = "True if the placing of this item is banned, false otherwise", required = true)
    public Boolean getPlacingBanned() {
        return placingBanned;
    }

    private Boolean dropBanned;
    @ApiModelProperty(value = "True if dropping this item is banned, false otherwise", required = true)
    public Boolean getDropBanned() {
        return dropBanned;
    }

    private Boolean worldBanned;
    @ApiModelProperty(value = "True if this item is banned from the world, false otherwise?", required = true)
    public Boolean getWorldBanned() {
        return worldBanned;
    }


    public CachedItemData() {
        super(null);
    }
    public CachedItemData(ItemData value) {
        super(value);

        this.item = new CachedCatalogType<>(Sponge.getRegistry().getType(ItemType.class, value.getItemid()).orElse(null));
        this.banReason = value.getBanreason();
        this.ownershipBanned = value.getOwnershipbanned();
        this.usageBanned = value.getUsagebanned();
        this.breakingBanned = value.getBreakingbanned();
        this.placingBanned = value.getPlacingbanned();
        this.dropBanned = value.getDropbanned();
        this.worldBanned = value.getWorldbanned();
    }

    @Override
    public Optional<ItemData> getLive() {
        return Optional.of(new ItemData(
                item.getId(),
                item.getName(),
                banReason != null ? banReason : "",
                usageBanned != null ? usageBanned : true,
                breakingBanned != null ? breakingBanned : false,
                placingBanned != null ? placingBanned : false,
                ownershipBanned != null ? ownershipBanned : true,
                dropBanned != null ? dropBanned : false,
                worldBanned != null ? worldBanned : false));
    }

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/mmc-restrict/item/" + item.getId();
    }
}
