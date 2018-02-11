package valandur.webapi.serialize.view.economy;

import io.swagger.annotations.ApiModel;
import org.spongepowered.api.service.economy.Currency;
import valandur.webapi.api.serialize.BaseView;

@ApiModel("Currency")
public class CurrencyView extends BaseView<Currency> {

    public String getId() {
        return value.getId();
    }

    public String getName() {
        return value.getName();
    }

    public String getDisplayName() {
        return value.getDisplayName().toPlain();
    }

    public String getPluralDisplayName() {
        return value.getPluralDisplayName().toPlain();
    }

    public String getSymbol() {
        return value.getSymbol().toPlain();
    }

    public Integer getDefaultFractionDigits() {
        return value.getDefaultFractionDigits();
    }


    public CurrencyView(Currency value) {
        super(value);
    }
}
