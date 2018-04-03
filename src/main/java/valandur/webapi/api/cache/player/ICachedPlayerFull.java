package valandur.webapi.api.cache.player;

import com.flowpowered.math.vector.Vector3d;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.api.cache.misc.ICachedInventory;
import valandur.webapi.api.cache.world.CachedLocation;

import java.util.List;

@ApiModel(value = "PlayerFull", parent = ICachedPlayer.class)
public interface ICachedPlayerFull extends ICachedPlayer {

    @ApiModelProperty(value = "The player's IP address and port", required = true)
    String getAddress();

    @ApiModelProperty(value = "The latency (in milliseconds) of the player", required = true)
    int getLatency();

    @ApiModelProperty(value = "The current Location of the player", required = true)
    CachedLocation getLocation();

    @ApiModelProperty(value = "The current rotation of the player", required = true)
    Vector3d getRotation();

    @ApiModelProperty(value = "The current velocity of the player", required = true)
    Vector3d getVelocity();

    @ApiModelProperty(value = "The current scale of the player", required = true)
    Vector3d getScale();

    @ApiModelProperty("The item stack that the player is wearing as a helmet")
    ItemStack getHelmet();

    @ApiModelProperty("The item stack that the player is wearing as chestplate")
    ItemStack getChestplate();

    @ApiModelProperty("The item stack that the player is wearing as leggings")
    ItemStack getLeggings();

    @ApiModelProperty("The item stack that the player is wearing as boots")
    ItemStack getBoots();

    @ApiModelProperty(value = "The current inventory of the player", required = true)
    ICachedInventory getInventory();
}
