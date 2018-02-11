package valandur.webapi.serialize.view.economy;

import io.swagger.annotations.ApiModel;
import org.spongepowered.api.service.economy.account.Account;
import valandur.webapi.api.serialize.BaseView;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@ApiModel("Account")
public class AccountView extends BaseView<Account> {

    public String getId() {
        return value.getIdentifier();
    }

    public String getFriendlyId() {
        return value.getFriendlyIdentifier().orElse(null);
    }

    public String getDisplayName() {
        return value.getDisplayName().toPlain();
    }

    public Map<String, BigDecimal> getBalances() {
        return value.getBalances().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue));
    }


    public AccountView(Account value) {
        super(value);
    }
}
