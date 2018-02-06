package valandur.webapi.integration.mmcrestrict;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.moddedminecraft.mmcrestrict.Data.ItemData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.misc.CachedCatalogType;

import java.util.Optional;

@JsonDeserialize
public class CachedItemData extends CachedObject<ItemData> {

    @JsonDeserialize
    private CachedCatalogType<ItemType> item;
    public CachedCatalogType<ItemType> getItem() {
        return item;
    }
    public void setItem(CachedCatalogType<ItemType> item) {
        this.item = item;
    }

    @JsonDeserialize
    private String banReason;
    public String getBanReason() {
        return banReason;
    }

    @JsonDeserialize
    private Boolean ownershipBanned;
    public Boolean getOwnershipBanned() {
        return ownershipBanned;
    }

    @JsonDeserialize
    private Boolean usageBanned;
    public Boolean getUsageBanned() {
        return usageBanned;
    }

    @JsonDeserialize
    private Boolean breakingBanned;
    public Boolean getBreakingBanned() {
        return breakingBanned;
    }

    @JsonDeserialize
    private Boolean placingBanned;
    public Boolean getPlacingBanned() {
        return placingBanned;
    }

    @JsonDeserialize
    private Boolean dropBanned;
    public Boolean getDropBanned() {
        return dropBanned;
    }

    @JsonDeserialize
    private Boolean worldBanned;
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
}
