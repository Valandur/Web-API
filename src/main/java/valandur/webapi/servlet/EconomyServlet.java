package valandur.webapi.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import valandur.webapi.cache.economy.CachedAccount;
import valandur.webapi.cache.economy.CachedCurrency;
import valandur.webapi.servlet.base.BaseServlet;
import valandur.webapi.servlet.base.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("economy")
@Api(tags = { "Economy" }, value = "Manage the economy on your server")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class EconomyServlet extends BaseServlet {

    @GET
    @Path("/currency")
    @Permission({ "currency", "list" })
    @ApiOperation(
            value = "List currencies",
            notes = "Lists all the currencies that the current economy supports.")
    public Collection<CachedCurrency> getCurrencies() {
        EconomyService srv = getEconomyService();
        return srv.getCurrencies().stream().map(CachedCurrency::new).collect(Collectors.toList());
    }

    @GET
    @Path("/account/{id}")
    @Permission({ "account", "one" })
    @ApiOperation(
            value = "List currencies",
            notes = "Lists all the currencies that the current economy supports.")
    public CachedAccount getAccount(@PathParam("id") String id) {
        EconomyService srv = getEconomyService();
        if (!srv.hasAccount(id))
            throw new NotFoundException("Could not find account with id " + id);

        Optional<Account> optAcc = srv.getOrCreateAccount(id);
        if (!optAcc.isPresent())
            throw new InternalServerErrorException("Could not get account " + id);

        return new CachedAccount(optAcc.get());
    }



    private EconomyService getEconomyService() {
        Optional<EconomyService> optSrv = Sponge.getServiceManager().provide(EconomyService.class);
        if (!optSrv.isPresent())
            throw new NotFoundException("Economy service was not found");
        return optSrv.get();
    }
}
