package valandur.webapi.integration.gwmcrates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.gwmdevelopments.sponge_plugin.crates.drop.Drop;
import org.gwmdevelopments.sponge_plugin.crates.drop.drops.*;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import valandur.webapi.cache.CachedObject;

import java.math.BigDecimal;
import java.util.stream.Collectors;

public class CachedDrop extends CachedObject<Drop> {

    private String type;
    public String getType() {
        return type;
    }

    private ItemStack displayItem;
    public ItemStack getDisplayItem() {
        return displayItem;
    }

    private int level;
    public int getLevel() {
        return level;
    }

    private Integer fakeLevel;
    public Integer getFakeLevel() {
        return fakeLevel;
    }

    private Double price;
    public Double getPrice() {
        return price;
    }

    private Currency currency;
    public Currency getCurrency() {
        return currency;
    }


    public CachedDrop(Drop value) {
        super(value);

        this.type = value.getType();
        this.displayItem = value.getDropItem().map(ItemStack::copy).orElse(null);
        this.level = value.getLevel();
        this.fakeLevel = value.getFakeLevel().orElse(null);
        this.price = value.getPrice().map(BigDecimal::doubleValue).orElse(null);
        this.currency = value.getSellCurrency().orElse(null);

        if (value instanceof ItemDrop) {
            data.put("item", ((ItemDrop) value).getItem().copy());
        } else if (value instanceof CommandsDrop) {
            data.put("commands", ((CommandsDrop) value).getExecutableCommands().stream()
                    .map(CachedExecCommand::new).collect(Collectors.toList()));
        } else if (value instanceof MultiDrop) {
            data.put("drops", ((MultiDrop) value).getDrops().stream()
                    .map(CachedDrop::new).collect(Collectors.toList()));
            data.put("giveAll", ((MultiDrop) value).isGiveAll());
        } else if (value instanceof DelayDrop) {
            data.put("drop", new CachedDrop(((DelayDrop) value).getChildDrop()));
            data.put("delay", ((DelayDrop) value).getDelay());
        } else if (value instanceof PermissionDrop) {
            data.put("permission", ((PermissionDrop) value).getPermission());
            data.put("dropTrue", new CachedDrop(((PermissionDrop) value).getDrop1()));
            data.put("dropFalse", new CachedDrop(((PermissionDrop) value).getDrop2()));
        }
    }

    @Override
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public String getLink() {
        return null;
    }
}
