package valandur.webapi.cache.player;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementProgress;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.player.ICachedAdvancement;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.cache.world.CachedLocation;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.cache.misc.CachedInventory;
import valandur.webapi.util.Constants;

import java.util.ArrayList;
import java.util.List;
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
    @JsonDetails
    public String getAddress() {
        return address;
    }

    private int latency;
    @JsonDetails
    public int getLatency() {
        return latency;
    }

    private CachedLocation location;
    public CachedLocation getLocation() {
        return location;
    }

    private Vector3d rotation;
    @JsonDetails
    public Vector3d getRotation() {
        return rotation;
    }

    private Vector3d velocity;
    @JsonDetails
    public Vector3d getVelocity() {
        return velocity;
    }

    private Vector3d scale;
    @JsonDetails
    public Vector3d getScale() {
        return scale;
    }

    private ItemStack helmet;
    @JsonDetails(simple = true)
    public ItemStack getHelmet() {
        return helmet;
    }

    private ItemStack chestplate;
    @JsonDetails(simple = true)
    public ItemStack getChestplate() {
        return chestplate;
    }

    private ItemStack leggings;
    @JsonDetails(simple = true)
    public ItemStack getLeggings() {
        return leggings;
    }

    private ItemStack boots;
    @JsonDetails(simple = true)
    public ItemStack getBoots() {
        return boots;
    }

    private CachedInventory inventory;
    @JsonDetails
    public CachedInventory getInventory() {
        return inventory;
    }

    private List<ICachedAdvancement> unlockedAdvancements = new ArrayList<>();
    @JsonDetails
    public List<ICachedAdvancement> getUnlockedAdvancements() {
        return unlockedAdvancements;
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

        // Collect unlocked advancements
        for (AdvancementTree tree : player.getUnlockedAdvancementTrees()) {
            addUnlockedAdvancements(player, tree.getRootAdvancement());
        }

        // This will be moved to the other constructor once Sponge implements the offline inventory API
        this.helmet = player.getHelmet().map(ItemStack::copy).orElse(null);
        this.chestplate = player.getChestplate().map(ItemStack::copy).orElse(null);
        this.leggings = player.getLeggings().map(ItemStack::copy).orElse(null);
        this.boots = player.getBoots().map(ItemStack::copy).orElse(null);
        this.inventory = new CachedInventory(player.getInventory());
    }

    private void addUnlockedAdvancements(Player p, Advancement a) {
        AdvancementProgress progress = p.getProgress(a);
        if (progress.achieved()) {
            unlockedAdvancements.add(new CachedAdvancement(a));
        }

        for (Advancement child : a.getChildren()) {
            addUnlockedAdvancements(p, child);
        }
    }

    @Override
    public Optional<Player> getLive() {
        return Sponge.getServer().getPlayer(uuid);
    }

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/player/" + uuid;
    }
}
