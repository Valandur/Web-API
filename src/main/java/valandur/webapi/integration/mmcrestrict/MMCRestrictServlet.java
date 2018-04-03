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
import valandur.webapi.api.cache.misc.CachedCatalogType;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Path("mmc-restrict")
@Api(tags = { "MMC Restrict" }, value = "Manage restricted items on your server.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class MMCRestrictServlet extends BaseServlet {

    public static void onRegister() {
        WebAPIAPI.getJsonService().ifPresent(srv -> {
            srv.registerCache(ItemData.class, CachedItemData.class);
        });
    }

    private Main getMMCRestrictPlugin() {
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
    @ApiOperation(
            value = "List items",
            notes = "Gets the list of restricted items on the server.")
    public Collection<CachedItemData> listItems() {
        Main plugin = getMMCRestrictPlugin();

        return WebAPIAPI.runOnMain(() -> {
            List<CachedItemData> items = new ArrayList<>();
            for (ItemData item : plugin.getItemData()) {
                items.add(new CachedItemData(item));
            }
            return items;
        });
    }

    @POST
    @Path("/item")
    @Permission({ "item", "create" })
    @ApiOperation(
            value = "Add an item",
            response = CachedItemData.class,
            notes = "Adds a new item to the restricted item list.")
    public Response addItem(CachedItemData req)
            throws BadRequestException, URISyntaxException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        if (req.getItem() == null) {
            throw new BadRequestException("Invalid item data");
        }

        CachedItemData item = WebAPIAPI.runOnMain(() -> {
            Main plugin = getMMCRestrictPlugin();
            Optional<ItemData> optData = req.getLive();
            if (!optData.isPresent()) {
                return null;
            }
            plugin.addItem(optData.get());
            saveData(plugin);
            return new CachedItemData(optData.get());
        });

        return Response.created(new URI(null, null, item.getLink(), null)).entity(item).build();
    }

    @PUT
    @Path("/item/{id}")
    @Permission({ "item", "modify" })
    @ApiOperation(
            value = "Modify an item",
            notes = "Modify an existing restricted item.")
    public CachedItemData modifyItem(@PathParam("id") String id, CachedItemData req)
            throws NotFoundException {

        if (req == null) {
            throw new BadRequestException("Request body is required");
        }

        return WebAPIAPI.runOnMain(() -> {
            Main plugin = getMMCRestrictPlugin();
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
    }

    @DELETE
    @Path("/item/{id}")
    @Permission({ "item", "delete" })
    @ApiOperation(
            value = "Delete an item",
            notes = "Delete an existing restricted item.")
    public CachedItemData deleteItem(@PathParam("id") String id)
            throws NotFoundException {

        return WebAPIAPI.runOnMain(() -> {
            Main plugin = getMMCRestrictPlugin();
            ItemData item = plugin.removeItem(id);
            if (item == null) {
                throw new NotFoundException("The specified item is not restricted");
            }
            saveData(plugin);
            return new CachedItemData(item);
        });
    }

    private void saveData(Main plugin) {
        try {
            plugin.saveData();
        } catch (IOException | ObjectMappingException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
