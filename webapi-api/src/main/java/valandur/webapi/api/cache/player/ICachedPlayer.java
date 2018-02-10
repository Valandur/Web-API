package valandur.webapi.api.cache.player;

import com.flowpowered.math.vector.Vector3d;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.misc.ICachedInventory;
import valandur.webapi.api.cache.world.CachedLocation;

import java.util.List;
import java.util.UUID;

@ApiModel("Player")
public interface ICachedPlayer extends ICachedObject<Player> {

    @ApiModelProperty(value = "The unique UUID of this player", required = true)
    UUID getUUID();

    @ApiModelProperty(value = "The players name", required = true)
    String getName();

    @ApiModelProperty(value = "True if the player is online, false otherwise", required = true)
    boolean isOnline();

    @ApiModelProperty("The player's IP address and port")
    String getAddress();

    @ApiModelProperty("The latency (in milliseconds) of the player")
    int getLatency();

    @ApiModelProperty(value = "The current Location of the player", required = true)
    CachedLocation getLocation();

    @ApiModelProperty("The current rotation of the player")
    Vector3d getRotation();

    @ApiModelProperty("The current velocity of the player")
    Vector3d getVelocity();

    @ApiModelProperty("The current scale of the player")
    Vector3d getScale();

    @ApiModelProperty("The item stack that the player is wearing as a helmet")
    ItemStack getHelmet();

    @ApiModelProperty("The item stack that the player is wearing as chestplate")
    ItemStack getChestplate();

    @ApiModelProperty("The item stack that the player is wearing as leggings")
    ItemStack getLeggings();

    @ApiModelProperty("The item stack that the player is wearing as boots")
    ItemStack getBoots();

    @ApiModelProperty("The current inventory of the player")
    ICachedInventory getInventory();

    @ApiModelProperty("A list of all unlocked advancements of this player")
    List<ICachedAdvancement> getUnlockedAdvancements();
}
