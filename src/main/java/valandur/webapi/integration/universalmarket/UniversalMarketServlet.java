package valandur.webapi.integration.universalmarket;

import com.xwaffle.universalmarket.UniversalMarket;
import com.xwaffle.universalmarket.market.Market;
import com.xwaffle.universalmarket.market.MarketItem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("universal-market")
@Api(tags = { "Universal Market" }, value = "View the current items on the market")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class UniversalMarketServlet extends BaseServlet {

    public static void onRegister() {
        WebAPIAPI.getJsonService().ifPresent(srv -> {
            srv.registerCache(MarketItem.class, CachedMarketItem.class);
        });
    }

    @GET
    @Path("/item")
    @Permission({ "item", "list" })
    @ApiOperation(
            value = "List items",
            notes = "Lists all the items in the market currently available for sale")
    public Collection<CachedMarketItem> listMarketItems() {
        return WebAPIAPI.runOnMain(() -> {
            UniversalMarket plugin = getUMPlugin();
            Market market = plugin.getMarket();
            return market.getListings().stream().map(CachedMarketItem::new).collect(Collectors.toList());
        });
    }

    private UniversalMarket getUMPlugin() {
        Optional<PluginContainer> optContainer = Sponge.getPluginManager().getPlugin("universalmarket");
        if (!optContainer.isPresent()) {
            throw new InternalServerErrorException("UniversalMarket plugin not found");
        }

        Optional<?> optPlugin = optContainer.get().getInstance();
        if (!optPlugin.isPresent()) {
            throw new InternalServerErrorException("UniversalMarket plugin instance not found");
        }

        return (UniversalMarket)optPlugin.get();
    }
}
