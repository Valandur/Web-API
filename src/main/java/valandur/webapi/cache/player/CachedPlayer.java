package valandur.webapi.cache.player;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.api.cache.misc.ICachedInventory;
import valandur.webapi.api.cache.misc.ICachedLocation;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedInventory;
import valandur.webapi.cache.misc.CachedLocation;

import java.util.Optional;
import java.util.UUID;

public class CachedPlayer extends CachedObject implements ICachedPlayer {

    protected UUID uuid;
    @Override
    public UUID getUUID() {
        return uuid;
    }

    private String name;
    @Override
    public String getName() {
        return name;
    }

    private boolean isOnline;
    @Override
    public boolean isOnline() {
        return isOnline;
    }

    private String address;
    @Override
    public String getAddress() {
        return address;
    }

    private int latency;
    @Override
    public int getLatency() {
        return latency;
    }

    private ICachedLocation location;
    @Override
    public ICachedLocation getLocation() {
        return location;
    }

    private Vector3d rotation;
    @Override
    public Vector3d getRotation() {
        return rotation;
    }

    private Vector3d velocity;
    @Override
    public Vector3d getVelocity() {
        return velocity;
    }

    private Vector3d scale;
    @Override
    public Vector3d getScale() {
        return scale;
    }

    private ItemStack helmet;
    @Override
    public ItemStack getHelmet() {
        return helmet;
    }

    private ItemStack chestplate;
    @Override
    public ItemStack getChestplate() {
        return chestplate;
    }

    private ItemStack leggings;
    @Override
    public ItemStack getLeggings() {
        return leggings;
    }

    private ItemStack boots;
    @Override
    public ItemStack getBoots() {
        return boots;
    }

    private ICachedInventory inventory;
    @Override
    public ICachedInventory getInventory() {
        return inventory;
    }


    public CachedPlayer(User user) {
        super(user);

        this.uuid = UUID.fromString(user.getUniqueId().toString());
        this.name = user.getName();
        this.isOnline = false;
    }
    public CachedPlayer(Player player) {
        this((User)player);

        this.isOnline = true;

        this.location = new CachedLocation(player.getLocation());
        this.rotation = player.getRotation().clone();
        this.velocity = player.getVelocity().clone();
        this.scale = player.getScale().clone();

        this.address = player.getConnection().getAddress().toString();
        this.latency = player.getConnection().getLatency();

        // This will be moved to the other constructor once Sponge implements the offline inventory API
        this.helmet = player.getHelmet().map(ItemStack::copy).orElse(null);
        this.chestplate = player.getChestplate().map(ItemStack::copy).orElse(null);
        this.leggings = player.getLeggings().map(ItemStack::copy).orElse(null);
        this.boots = player.getBoots().map(ItemStack::copy).orElse(null);
        this.inventory = new CachedInventory(player.getInventory());
    }

    @Override
    public Optional<?> getLive() {
        return Sponge.getServer().getPlayer(uuid);
    }

    @Override
    public String getLink() {
        return "/api/player/" + uuid;
    }
}
