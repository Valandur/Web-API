package valandur.webapi.integration.villagershops;

import com.flowpowered.math.vector.Vector3d;
import de.dosmike.sponge.vshop.*;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import valandur.webapi.integration.villagershops.wrapper.CachedStockItem;
import valandur.webapi.integration.villagershops.wrapper.CachedVShop;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.Optional;

class VShopCompareUtils {

    @SuppressWarnings({"rawtypes", "unchecked"})
    static void applyDiv(CachedVShop update, NPCguard shop) {
        Entity rep = shop.getLe();
        boolean respawnShop = false; //if the entity changed a new entity / mob has to be spawned
        if (update.getEntityType() != null && !update.getEntityType().getId().equalsIgnoreCase(shop.getNpcType().toString())) {
            EntityType et = update.getEntityType().getLive(EntityType.class).orElse(null);
            if (et == null)
                throw new BadRequestException("EntityType " + update.getEntityType() + " is unknown");
            respawnShop = true;
            shop.setNpcType(et);
        }
        if (update.getEntityVariant() != null && !update.getEntityVariant().equalsIgnoreCase(shop.getVariantName())) {
            respawnShop = true;
            shop.setVariant(update.getEntityVariant());
        }
        if (update.getLocation() != null) {
            Optional<Location> ol = update.getLocation().getLive();
            if (!ol.isPresent())
                throw new BadRequestException("Could not get Live version of Location");
            shop.move(ol.get());
        }
        if (update.getName() != null && !update.getName().equals(TextSerializers.FORMATTING_CODE.serialize(shop.getDisplayName()))) {
            shop.setDisplayName(TextSerializers.FORMATTING_CODE.deserialize(update.getName()));
        }
        if (update.getOwner() != null && !update.getOwner().equals(shop.getShopOwner().orElse(null))) {
//			if (update.isPlayerShop() == null)
//				throw new BadRequestException("Missing flag playershop");
//			if (!update.isPlayerShop()) {
//				throw new BadRequestException("Can't set owner of non player-shops");
//			} else {
            shop.setPlayerShop(update.getOwner());
//			}
//		} else if (!update.isPlayerShop()) {
        } else if (update.getOwner() == null && shop.getShopOwner().isPresent()) {
            shop.setPlayerShop(null);
//		} else {
//			throw new BadRequestException("Missing owner UUID for player-shop");
        }
        if (update.getRotation() != null && update.getRotation() != shop.getRot().getY()) {
            shop.setRot(new Vector3d(0.0, update.getRotation(), 0.0));
        }
        if (update.getStockContainer() == null && shop.getStockContainer().isPresent()) {
            API.playershop(shop, null, null);
        } else if (update.getStockContainer() != null) {
            Optional<Location> ol = update.getLocation().getLive();
            if (!ol.isPresent())
                throw new BadRequestException("Could not get Live version of Stock Location");
            if (!ol.get().equals(shop.getStockContainer().orElse(null))) {
                API.playershop(shop, shop.getShopOwner().get(), ol.get());
            }
        }

        if (update.getStockItems() != null) {
            VillagerShops.closeShopInventories(shop.getIdentifier());
            Optional<NPCguard> npc = VillagerShops.getNPCfromShopUUID(shop.getIdentifier());
            if (!npc.isPresent()) {
                throw new NotFoundException("Shop with id " + shop.getIdentifier() + " not found");
            }
            InvPrep inv = npc.get().getPreparator();
            int s = inv.size();
            int newS = update.getStockItems().size();
            for (int i = 0; i < Math.max(s, newS); i++) {
                if (i >= s) {
                    CachedStockItem item = update.getStockItems().get(i);
                    inv.addItem(new StockItem(
                            item.getItem().createStack(),
                            item.getSellPrice(),
                            item.getBuyPrice(),
                            VillagerShops.getInstance().CurrencyByName(item.getCurrency().getId()),
                            item.getMaxStock()));
                } else if (i >= newS) {
                    inv.removeIndex(i);
                } else {
                    CachedStockItem item = update.getStockItems().get(i);
                    inv.setItem(i, new StockItem(
                            item.getItem().createStack(),
                            item.getSellPrice(),
                            item.getBuyPrice(),
                            VillagerShops.getInstance().CurrencyByName(item.getCurrency().getId()),
                            item.getMaxStock()));
                }
            }
        }

        if (respawnShop && rep != null && !rep.isRemoved()) {
            rep.remove();
            //tick will respawn the new entity
        }
    }

}
