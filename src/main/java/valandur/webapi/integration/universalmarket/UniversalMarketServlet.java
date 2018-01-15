package valandur.webapi.integration.universalmarket;

import com.xwaffle.universalmarket.UniversalMarket;
import com.xwaffle.universalmarket.market.Market;
import com.xwaffle.universalmarket.market.MarketItem;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.WebAPI;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.IServletData;
import valandur.webapi.api.servlet.Servlet;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Servlet(basePath = "universalmarket")
public class UniversalMarketServlet extends BaseServlet {

    public static void onRegister() {
        WebAPIAPI.getJsonService().ifPresent(srv -> {
            srv.registerCache(MarketItem.class, CachedMarketItem.class);
        });
    }

    @Endpoint(method = HttpMethod.GET, path = "/item", perm = "um.item.list")
    public void getMarketItems(IServletData data) {
        UniversalMarket plugin = getUMPlugin(data);
        if (plugin == null) return;

        Optional<List<CachedMarketItem>> optList = WebAPI.runOnMain(() -> {
            Market market = plugin.getMarket();
            return market.getListings().stream().map(CachedMarketItem::new).collect(Collectors.toList());
        });

        data.addData("ok", optList.isPresent(), false);
        data.addData("items", optList.orElse(null), data.getQueryParam("details").isPresent());
    }

    private UniversalMarket getUMPlugin(IServletData data) {
        Optional<PluginContainer> optContainer = Sponge.getPluginManager().getPlugin("universalmarket");
        if (!optContainer.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "UniversalMarket plugin not found");
            return null;
        }

        Optional<?> optPlugin = optContainer.get().getInstance();
        if (!optPlugin.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "UniversalMarket plugin instance not found");
            return null;
        }

        return (UniversalMarket)optPlugin.get();
    }
}
