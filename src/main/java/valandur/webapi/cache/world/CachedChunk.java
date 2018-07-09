package valandur.webapi.cache.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.world.Chunk;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedVector3i;
import valandur.webapi.serialize.JsonDetails;
import valandur.webapi.util.Constants;

import javax.ws.rs.NotFoundException;
import java.util.Optional;
import java.util.UUID;

@ApiModel("Chunk")
public class CachedChunk extends CachedObject<Chunk> {

    private UUID uuid;
    @ApiModelProperty(value = "The unique id of this chunk", required = true)
    public UUID getUUID() {
        return uuid;
    }

    private CachedVector3i pos;
    @ApiModelProperty(value = "The position of this chunk (in chunk coordinates)", required = true)
    public CachedVector3i getPosition() {
        return pos;
    }

    private CachedWorld world;
    @JsonDetails(simple = true)
    @ApiModelProperty("The world the chunk is in")
    public CachedWorld getWorld() {
        return world;
    }

    private boolean loaded;
    @ApiModelProperty(value = "True if this chunk is currently loaded, false otherwise", required = true)
    public boolean isLoaded() {
        return loaded;
    }

    private CachedVector3i blockMin;
    @JsonDetails
    @ApiModelProperty("The bock with the smallest coordinates that is still part of this chunk")
    public CachedVector3i getBlockMin() {
        return blockMin;
    }

    private CachedVector3i blockMax;
    @JsonDetails
    @ApiModelProperty("The bock with the largest coordinates that is still part of this chunk")
    public CachedVector3i getBlockMax() {
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

        this.uuid = chunk.getUniqueId();
        this.pos = new CachedVector3i(chunk.getPosition());
        this.world = WebAPI.getCacheService().getWorld(chunk.getWorld());
        this.blockMin = new CachedVector3i(chunk.getBlockMin());
        this.blockMax = new CachedVector3i(chunk.getBlockMax());
        this.loaded = chunk.isLoaded();
        this.inhabitedTime = chunk.getInhabitedTime();
        this.regionalDifficultyFactor = chunk.getRegionalDifficultyFactor();
        this.regionalDifficultyPercentage = chunk.getRegionalDifficultyPercentage();
    }

    @Override
    public Chunk getLive() {
        Optional<Chunk> optChunk = world.getLive().getChunk(pos.getLive());
        if (!optChunk.isPresent()) {
            throw new NotFoundException("Could not find chunk: " + uuid);
        }
        return optChunk.get();
    }

    @Override
    @JsonIgnore(false)
    public String getLink() {
        return Constants.BASE_PATH + "/chunk/" + world.getUUID() + "/" + pos.x + "/" + pos.z;
    }
}
