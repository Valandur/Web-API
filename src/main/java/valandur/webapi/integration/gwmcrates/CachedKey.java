package valandur.webapi.integration.gwmcrates;

import org.gwmdevelopments.sponge_plugin.crates.key.Key;
import org.gwmdevelopments.sponge_plugin.crates.key.keys.*;
import org.spongepowered.api.service.economy.Currency;
import valandur.webapi.cache.CachedObject;

import java.math.BigDecimal;
import java.util.stream.Collectors;

public class CachedKey extends CachedObject<Key> {

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


    public CachedKey(Key value) {
        super(value);

        this.type = value.getType();
        this.price = value.getPrice().map(BigDecimal::doubleValue).orElse(null);
        this.currency = value.getSellCurrency().orElse(null);

        if (value instanceof ItemKey) {
            data.put("item", ((ItemKey) value).getItem().copy());
        } else if (value instanceof MultiKey) {
            data.put("keys",
                    ((MultiKey) value).getKeys().stream().map(CachedKey::new).collect(Collectors.toList()));
            data.put("allKeysNeeded", ((MultiKey) value).isAllKeysNeeded());
        } else if (value instanceof MultipleAmountKey) {
            data.put("amount", ((MultipleAmountKey) value).getAmount());
            data.put("key", new CachedKey(((MultipleAmountKey) value).getChildKey()));
        } else if (value instanceof VirtualKey) {
            data.put("name", ((VirtualKey) value).getVirtualName());
        } else if (value instanceof TimedKey) {
            data.put("name", ((TimedKey) value).getVirtualName());
            data.put("delay", ((TimedKey) value).getDelay());
        }
    }
}
