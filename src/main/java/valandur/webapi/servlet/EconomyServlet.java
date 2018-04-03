package valandur.webapi.servlet;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Optional;

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
    public Collection<Currency> getCurrencies() {
        EconomyService srv = getEconomyService();
        return srv.getCurrencies();
    }

    @GET
    @Path("/account/{id}")
    @Permission({ "account", "one" })
    @ApiOperation(
            value = "List currencies",
            notes = "Lists all the currencies that the current economy supports.")
    public Account getAccount(@PathParam("id") String id) {
        EconomyService srv = getEconomyService();
        if (!srv.hasAccount(id))
            throw new NotFoundException("Could not find account with id " + id);

        Optional<Account> optAcc = srv.getOrCreateAccount(id);
        if (!optAcc.isPresent())
            throw new InternalServerErrorException("Could not get account " + id);

        return optAcc.get();
    }



    private EconomyService getEconomyService() {
        Optional<EconomyService> optSrv = Sponge.getServiceManager().provide(EconomyService.class);
        if (!optSrv.isPresent())
            throw new NotFoundException("Economy service was not found");
        return optSrv.get();
    }
}
