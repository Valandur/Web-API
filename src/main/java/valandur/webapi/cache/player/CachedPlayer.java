package valandur.webapi.cache.player;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.cache.misc.CachedInventory;
import valandur.webapi.api.cache.world.CachedLocation;

import java.util.Optional;
import java.util.UUID;

public class CachedPlayer extends CachedObject<Player> implements ICachedPlayer {

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
    public boolean isOnline() {
        return isOnline;
    }

    private String address;
    public String getAddress() {
        return address;
    }

    private int latency;
    public int getLatency() {
        return latency;
    }

    private CachedLocation location;
    public CachedLocation getLocation() {
        return location;
    }

    private Vector3d rotation;
    public Vector3d getRotation() {
        return rotation;
    }

    private Vector3d velocity;
    public Vector3d getVelocity() {
        return velocity;
    }

    private Vector3d scale;
    public Vector3d getScale() {
        return scale;
    }

    private ItemStack helmet;
    public ItemStack getHelmet() {
        return helmet;
    }

    private ItemStack chestplate;
    public ItemStack getChestplate() {
        return chestplate;
    }

    private ItemStack leggings;
    public ItemStack getLeggings() {
        return leggings;
    }

    private ItemStack boots;
    public ItemStack getBoots() {
        return boots;
    }

    private CachedInventory inventory;
    public CachedInventory getInventory() {
        return inventory;
    }


    public CachedPlayer(User user) {
        super(null);

        this.uuid = UUID.fromString(user.getUniqueId().toString());
        this.name = user.getName();
        this.isOnline = false;
    }
    public CachedPlayer(Player player) {
        super(player);

        this.uuid = UUID.fromString(player.getUniqueId().toString());
        this.name = player.getName();
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
    public Optional<Player> getLive() {
        return Sponge.getServer().getPlayer(uuid);
    }

    @Override
    public String getLink() {
        return "/api/player/" + uuid;
    }
}
