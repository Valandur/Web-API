package valandur.webapi.cache.world;

import com.flowpowered.math.vector.Vector3i;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Chunk;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.util.Constants;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

public class CachedChunk extends CachedObject<Chunk> {

    private UUID uuid;
    @ApiModelProperty(value = "The unique id of this chunk", required = true)
    public UUID getUUID() {
        return uuid;
    }

    private Vector3i pos;
    @ApiModelProperty(value = "The position of this chunk (in chunk coordinates)", required = true)
    public Vector3i getPosition() {
        return pos;
    }

    private ICachedWorld world;
    @JsonDetails(simple = true)
    @ApiModelProperty("The world the chunk is in")
    public ICachedWorld getWorld() {
        return world;
    }

    private boolean loaded;
    @ApiModelProperty(value = "True if this chunk is currently loaded, false otherwise", required = true)
    public boolean isLoaded() {
        return loaded;
    }

    private Vector3i blockMin;
    @JsonDetails
    @ApiModelProperty("The bock with the smallest coordinates that is still part of this chunk")
    public Vector3i getBlockMin() {
        return blockMin;
    }

    private Vector3i blockMax;
    @JsonDetails
    @ApiModelProperty("The bock with the largest coordinates that is still part of this chunk")
    public Vector3i getBlockMax() {
        return blockMax;
    }

    private int inhabitedTime;
    @JsonDetails
    @ApiModelProperty("The total amount of time (in server ticks) this chunk has been inhabited by players.")
    public int getInhabitedTime() {
        return inhabitedTime;
    }

    private double regionalDifficultyFactor;
    @JsonDetails
    @ApiModelProperty("The increase in difficulty due to the presence of players in the chunk")
    public double getRegionalDifficultyFactor() {
        return regionalDifficultyFactor;
    }

    private double regionalDifficultyPercentage;
    @JsonDetails
    @ApiModelProperty("The increase in difficulty due to the presence of players in the chunk as a percentage")
    public double getRegionalDifficultyPercentage() {
        return regionalDifficultyPercentage;
    }


    public CachedChunk(Chunk chunk) {
        super(chunk);

        this.uuid = UUID.fromString(chunk.getUniqueId().toString());
        this.pos = chunk.getPosition().clone();
        this.world = WebAPI.getCacheService().getWorld(chunk.getWorld());
        this.blockMin = chunk.getBlockMin().clone();
        this.blockMax = chunk.getBlockMax().clone();
        this.loaded = chunk.isLoaded();
        this.inhabitedTime = chunk.getInhabitedTime();
        this.regionalDifficultyFactor = chunk.getRegionalDifficultyFactor();
        this.regionalDifficultyPercentage = chunk.getRegionalDifficultyPercentage();
    }

    @Override
    public Optional<Chunk> getLive() {
        if (world.isLoaded()) {
            return Sponge.getServer().getWorld(uuid).flatMap(w -> w.getChunk(pos));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/chunk/" + world.getUUID() + "/" + pos.getX() + "/" + pos.getZ();
    }
}
