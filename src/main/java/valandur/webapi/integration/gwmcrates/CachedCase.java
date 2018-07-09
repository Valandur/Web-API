package valandur.webapi.integration.gwmcrates;

import org.gwmdevelopments.sponge_plugin.crates.caze.Case;
import org.gwmdevelopments.sponge_plugin.crates.caze.cases.BlockCase;
import org.gwmdevelopments.sponge_plugin.crates.caze.cases.EntityCase;
import org.gwmdevelopments.sponge_plugin.crates.caze.cases.ItemCase;
import org.gwmdevelopments.sponge_plugin.crates.caze.cases.VirtualCase;
import org.spongepowered.api.service.economy.Currency;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.world.CachedLocation;

import java.math.BigDecimal;

public class CachedCase extends CachedObject<Case> {

    private String type;
    public String getType() {
        return type;
    }

    private Double price;
    public Double getPrice() {
        return price;
    }

    private Currency currency;
    public Currency getCurrency() {
        return currency;
    }


    public CachedCase(Case value) {
        super(value);

        this.type = value.getType();
        this.price = value.getPrice().map(BigDecimal::doubleValue).orElse(null);
        this.currency = value.getSellCurrency().orElse(null);

        if (value instanceof ItemCase) {
            data.put("item", ((ItemCase) value).getItem().copy());
        } else if (value instanceof BlockCase) {
            data.put("location", new CachedLocation(((BlockCase) value).getLocation()));
            data.put("hologram", ((BlockCase) value).getHologram().orElse(null));
        } else if (value instanceof EntityCase) {
            data.put("uuid", ((EntityCase) value).getEntityUuid());
        } else if (value instanceof VirtualCase) {
            data.put("virtualName", ((VirtualCase) value).getVirtualName());
        }
    }
}
