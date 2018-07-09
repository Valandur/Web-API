package valandur.webapi.cache.economy;

import io.swagger.annotations.ApiModel;
import org.spongepowered.api.service.economy.Currency;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedText;

@ApiModel("Currency")
public class CachedCurrency extends CachedObject<Currency> {

    public String id;
    public String name;
    public CachedText displayName;
    public CachedText pluralDisplayName;
    public CachedText symbol;
    public Integer defaultFractionalDigits;


    public CachedCurrency(Currency value) {
        super(value);

        this.id = value.getId();
        this.name = value.getName();
        this.displayName = new CachedText(value.getDisplayName());
        this.pluralDisplayName = new CachedText(value.getPluralDisplayName());
        this.symbol = new CachedText(value.getSymbol());
        this.defaultFractionalDigits = value.getDefaultFractionDigits();
    }
}
