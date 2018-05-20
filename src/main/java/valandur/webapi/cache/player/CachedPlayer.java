package valandur.webapi.cache.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowpowered.math.vector.Vector3d;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementProgress;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.user.UserStorageService;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedInventory;
import valandur.webapi.cache.world.CachedLocation;
import valandur.webapi.serialize.JsonDetails;
import valandur.webapi.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApiModel(value = "Player")
public class CachedPlayer extends CachedObject<Player> {

    protected UUID uuid;
    @ApiModelProperty(value = "The unique UUID of this player", required = true)
    public UUID getUUID() {
        return uuid;
    }

    private String name;
    @ApiModelProperty(value = "The players name", required = true)
    public String getName() {
        return name;
    }

    private boolean isOnline;
    @ApiModelProperty(value = "True if the player is online, false otherwise", required = true)
    public boolean isOnline() {
        return isOnline;
    }

    private String address;
    @JsonDetails
    @ApiModelProperty(value = "The player's IP address and port", required = true)
    public String getAddress() {
        return address;
    }

    private int latency;
    @JsonDetails
    @ApiModelProperty(value = "The latency (in milliseconds) of the player", required = true)
    public int getLatency() {
        return latency;
    }

    private CachedLocation location;
    @ApiModelProperty(value = "The current Location of the player", required = true)
    public CachedLocation getLocation() {
        return location;
    }

    private Vector3d rotation;
    @JsonDetails
    @ApiModelProperty(value = "The current rotation of the player", required = true)
    public Vector3d getRotation() {
        return rotation;
    }

    private Vector3d velocity;
    @JsonDetails
    @ApiModelProperty(value = "The current velocity of the player", required = true)
    public Vector3d getVelocity() {
        return velocity;
    }

    private Vector3d scale;
    @JsonDetails
    @ApiModelProperty(value = "The current scale of the player", required = true)
    public Vector3d getScale() {
        return scale;
    }

    private ItemStack helmet;
    @JsonDetails(simple = true)
    @ApiModelProperty("The item stack that the player is wearing as a helmet")
    public ItemStack getHelmet() {
        return helmet;
    }

    private ItemStack chestplate;
    @JsonDetails(simple = true)
    @ApiModelProperty("The item stack that the player is wearing as chestplate")
    public ItemStack getChestplate() {
        return chestplate;
    }

    private ItemStack leggings;
    @JsonDetails(simple = true)
    @ApiModelProperty("The item stack that the player is wearing as leggings")
    public ItemStack getLeggings() {
        return leggings;
    }

    private ItemStack boots;
    @JsonDetails(simple = true)
    @ApiModelProperty("The item stack that the player is wearing as boots")
    public ItemStack getBoots() {
        return boots;
    }

    private CachedInventory inventory;
    @JsonDetails
    @ApiModelProperty(value = "The current inventory of the player", required = true)
    public CachedInventory getInventory() {
        return inventory;
    }

    private List<CachedAdvancement> unlockedAdvancements = new ArrayList<>();
    @JsonDetails
    @ApiModelProperty(value = "A list of all unlocked advancements of this player", required = true)
    public List<CachedAdvancement> getUnlockedAdvancements() {
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

    @JsonIgnore
    public Optional<User> getUser() {
        Optional<UserStorageService> optSrv = Sponge.getServiceManager().provide(UserStorageService.class);
        return optSrv.flatMap(srv -> srv.get(uuid));
    }

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/player/" + uuid;
    }
}
