package valandur.webapi.serialize.view.economy;

import io.swagger.annotations.ApiModel;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import valandur.webapi.serialize.BaseView;

@ApiModel("Currency")
public class CurrencyView extends BaseView<Currency> {

    public String getId() {
        return value.getId();
    }

    public String getName() {
        return value.getName();
    }

    public Text getDisplayName() {
        return value.getDisplayName();
    }

    public Text getPluralDisplayName() {
        return value.getPluralDisplayName();
    }

    public Text getSymbol() {
        return value.getSymbol();
    }

    public Integer getDefaultFractionDigits() {
        return value.getDefaultFractionDigits();
    }


    public CurrencyView(Currency value) {
        super(value);
    }
}
