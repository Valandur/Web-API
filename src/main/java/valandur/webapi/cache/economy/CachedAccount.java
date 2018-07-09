package valandur.webapi.cache.economy;

import io.swagger.annotations.ApiModel;
import org.spongepowered.api.service.economy.account.Account;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedText;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@ApiModel("Account")
public class CachedAccount extends CachedObject<Account> {

    public String id;
    public String friendlyName;
    public CachedText displayName;
    public Map<String, BigDecimal> balances;


    public CachedAccount(Account value) {
        super(value);

        this.id = value.getIdentifier();
        this.friendlyName = value.getFriendlyIdentifier().orElse(null);
        this.displayName = new CachedText(value.getDisplayName());
        this.balances =  value.getBalances().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue));
    }
}
