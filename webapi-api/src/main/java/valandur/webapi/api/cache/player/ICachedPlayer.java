package valandur.webapi.api.cache.player;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.misc.ICachedInventory;
import valandur.webapi.api.cache.misc.ICachedLocation;

import java.util.UUID;

public interface ICachedPlayer extends ICachedObject {

    UUID getUUID();

    String getName();

    String getAddress();

    int getLatency();

    boolean isOnline();

    ICachedLocation getLocation();

    Vector3d getRotation();

    Vector3d getVelocity();

    Vector3d getScale();

    ItemStack getHelmet();

    ItemStack getChestplate();

    ItemStack getLeggings();

    ItemStack getBoots();

    ICachedInventory getInventory();
}
