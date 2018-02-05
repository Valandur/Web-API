package valandur.webapi.integration.mmcrestrict;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.moddedminecraft.mmcrestrict.Data.ItemData;
import net.moddedminecraft.mmcrestrict.Main;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.cache.misc.CachedCatalogType;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Path("mmc-restrict")
@Api(tags = { "MMC Restrict "}, value = "Manage restricted items on your server.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class MMCRestrictServlet extends BaseServlet {

    public static void onRegister() {
        WebAPIAPI.getJsonService().ifPresent(srv -> {
            srv.registerCache(ItemData.class, CachedItemData.class);
        });
    }

    private Main getMMCRestrictPlugin() throws InternalServerErrorException {
        Optional<PluginContainer> optContainer = Sponge.getPluginManager().getPlugin("mmcrestrict");
        if (!optContainer.isPresent()) {
            throw new InternalServerErrorException("MMCRestrict plugin not found");
        }

        Optional<?> optPlugin = optContainer.get().getInstance();
        if (!optPlugin.isPresent()) {
            throw new InternalServerErrorException("MMCRestrict plugin instance not found");
        }

        return (Main)optPlugin.get();
    }

    @GET
    @Path("/item")
    @Permission({ "item", "list" })
    @ApiOperation(value = "List items", notes = "Gets the list of restricted items on the server.")
    public Collection<CachedItemData> getItems() {
        Main plugin = getMMCRestrictPlugin();

        Optional<List<CachedItemData>> optItems = WebAPIAPI.runOnMain(() -> {
            List<CachedItemData> items = new ArrayList<>();
            for (ItemData item : plugin.getItemData()) {
                items.add(new CachedItemData(item));
            }
            return items;
        });

        return optItems.orElse(null);
    }

    @POST
    @Path("/item")
    @Permission({ "item", "create" })
    @ApiOperation(value = "Create an item", notes = "Adds a new item to the restricted item list.")
    public CachedItemData addItem(CachedItemData req)
            throws BadRequestException, InternalServerErrorException {
        Main plugin = getMMCRestrictPlugin();

        if (req.getItem() == null) {
            throw new BadRequestException("Invalid item data");
        }

        Optional<CachedItemData> optItem = WebAPIAPI.runOnMain(() -> {
            Optional<ItemData> optData = req.getLive();
            if (!optData.isPresent()) {
                return null;
            }
            plugin.addItem(optData.get());
            saveData(plugin);
            return new CachedItemData(optData.get());
        });

        return optItem.orElse(null);
    }

    @PUT
    @Path("/item/{id}")
    @Permission({ "item", "change" })
    @ApiOperation(value = "Change an item", notes = "Change an existing restricted item.")
    public CachedItemData changeItem(@PathParam("id") String id, CachedItemData req)
            throws NotFoundException, InternalServerErrorException {
        Main plugin = getMMCRestrictPlugin();

        Optional<CachedItemData> optItem = WebAPIAPI.runOnMain(() -> {
            ItemData item = plugin.removeItem(id);
            if (item == null) {
                throw new NotFoundException("The specified item is not restricted");
            }
            // Transfer the item type of the old item to the new one
            Optional<ItemType> optType = Sponge.getRegistry().getType(ItemType.class, item.getItemid());
            if (!optType.isPresent()) {
                plugin.addItem(item);   // Add the old item because we removed it
                throw new InternalServerErrorException("Could not get item type");
            }
            req.setItem(new CachedCatalogType<>(optType.get()));
            Optional<ItemData> optData = req.getLive();
            if (!optData.isPresent()) {
                plugin.addItem(item);   // Add the old item because we removed it
                throw new InternalServerErrorException("Could not create item data");
            }
            plugin.addItem(optData.get());
            saveData(plugin);
            return new CachedItemData(optData.get());
        });

        return optItem.orElse(null);
    }

    @DELETE
    @Path("/item/{id}")
    @Permission({ "item", "delete" })
    @ApiOperation(value = "Delete an item", notes = "Delete an existing restricted item.")
    public CachedItemData deleteItem(@PathParam("id") String id)
            throws NotFoundException, InternalServerErrorException{
        Main plugin = getMMCRestrictPlugin();

        Optional<CachedItemData> optItem = WebAPIAPI.runOnMain(() -> {
            ItemData item = plugin.removeItem(id);
            if (item == null) {
                throw new NotFoundException("The specified item is not restricted");
            }
            saveData(plugin);
            return new CachedItemData(item);
        });

        return optItem.orElse(null);
    }

    private void saveData(Main plugin) throws InternalServerErrorException {
        try {
            plugin.saveData();
        } catch (IOException | ObjectMappingException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
