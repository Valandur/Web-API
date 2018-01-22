package valandur.webapi.integration.mmcrestrict;

import net.moddedminecraft.mmcrestrict.Data.ItemData;
import net.moddedminecraft.mmcrestrict.Main;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.IServletData;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.cache.misc.CachedCatalogType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Servlet(basePath = "mmcrestrict")
public class MMCRestrictServlet extends BaseServlet {

    public static void onRegister() {
        WebAPIAPI.getJsonService().ifPresent(srv -> {
            srv.registerCache(ItemData.class, CachedItemData.class);
        });
    }


    private Main getMMCRestrictPlugin(IServletData data) {
        Optional<PluginContainer> optContainer = Sponge.getPluginManager().getPlugin("mmcrestrict");
        if (!optContainer.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "MMCRestrict plugin not found");
            return null;
        }

        Optional<?> optPlugin = optContainer.get().getInstance();
        if (!optPlugin.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "MMCRestrict plugin instance not found");
            return null;
        }

        return (Main)optPlugin.get();
    }

    @Endpoint(method = HttpMethod.GET, path = "/item", perm = "list")
    public void getItems(IServletData data) {
        Main plugin = getMMCRestrictPlugin(data);
        if (plugin == null) return;

        Optional<List<CachedItemData>> optItems = WebAPIAPI.runOnMain(() -> {
            List<CachedItemData> items = new ArrayList<>();
            for (ItemData item : plugin.getItemData()) {
                items.add(new CachedItemData(item));
            }
            return items;
        });

        data.addData("ok", optItems.isPresent(), false);
        data.addData("items", optItems.orElse(null), data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.POST, path = "/item", perm = "create")
    public void addItem(IServletData data) {
        Main plugin = getMMCRestrictPlugin(data);
        if (plugin == null) return;

        Optional<CachedItemData> optReq = data.getRequestBody(CachedItemData.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid item data: " +
                    data.getLastParseError().getMessage());
            return;
        }

        CachedItemData req = optReq.get();
        if (req.getItem() == null) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid item data");
            return;
        }

        Optional<CachedItemData> optItem = WebAPIAPI.runOnMain(() -> {
            Optional<ItemData> optData = req.getLive();
            if (!optData.isPresent()) {
                data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not create item data");
                return null;
            }
            plugin.addItem(optData.get());
            if (!saveData(plugin, data)) {
                return null;
            }
            return new CachedItemData(optData.get());
        });

        data.addData("ok", optItem.isPresent(), false);
        data.addData("item", optItem.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.PUT, path = "/item/:id", perm = "change")
    public void changeItem(IServletData data, String id) {
        Main plugin = getMMCRestrictPlugin(data);
        if (plugin == null) return;

        Optional<CachedItemData> optReq = data.getRequestBody(CachedItemData.class);
        if (!optReq.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid item data: " +
                    data.getLastParseError().getMessage());
            return;
        }

        CachedItemData req = optReq.get();

        Optional<CachedItemData> optItem = WebAPIAPI.runOnMain(() -> {
            ItemData item = plugin.removeItem(id);
            if (item == null) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "The specified item is not restricted");
                return null;
            }
            // Transfer the item type of the old item to the new one
            Optional<ItemType> optType = Sponge.getRegistry().getType(ItemType.class, item.getItemid());
            if (!optType.isPresent()) {
                data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get item type");
                plugin.addItem(item);   // Add the old item because we removed it
                return null;
            }
            req.setItem(new CachedCatalogType<>(optType.get()));
            Optional<ItemData> optData = req.getLive();
            if (!optData.isPresent()) {
                data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not create item data");
                plugin.addItem(item);   // Add the old item because we removed it
                return null;
            }
            plugin.addItem(optData.get());
            if (!saveData(plugin, data)) {
                return null;
            }
            return new CachedItemData(optData.get());
        });

        data.addData("ok", optItem.isPresent(), false);
        data.addData("item", optItem.orElse(null), true);
    }

    @Endpoint(method = HttpMethod.DELETE, path = "/item/:id", perm = "delete")
    public void deleteItem(IServletData data, String id) {
        Main plugin = getMMCRestrictPlugin(data);
        if (plugin == null) return;

        Optional<CachedItemData> optItem = WebAPIAPI.runOnMain(() -> {
            ItemData item = plugin.removeItem(id);
            if (item == null) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "The specified item is not restricted");
                return null;
            }
            if (!saveData(plugin, data)) {
                return null;
            }
            return new CachedItemData(item);
        });

        data.addData("ok", optItem.isPresent(), false);
        data.addData("item", optItem.orElse(null), true);
    }

    private boolean saveData(Main plugin, IServletData data) {
        try {
            plugin.saveData();
            return true;
        } catch (IOException | ObjectMappingException e) {
            e.printStackTrace();
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return false;
        }
    }
}
