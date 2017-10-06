package valandur.webapi.cache.world;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Chunk;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.json.JsonDetails;

import java.util.Optional;
import java.util.UUID;

public class CachedChunk extends CachedObject<Chunk> {

    protected UUID uuid;
    public UUID getUUID() {
        return uuid;
    }

    protected Vector3i pos;
    public Vector3i getPosition() {
        return pos;
    }

    @JsonDetails(simple = true)
    protected ICachedWorld world;
    public ICachedWorld getWorld() {
        return world;
    }

    @JsonDetails
    protected boolean loaded;
    public boolean isLoaded() {
        return loaded;
    }

    @JsonDetails
    protected Vector3i blockMin;

    @JsonDetails
    protected Vector3i blockMax;

    @JsonDetails
    protected int inhabitedTime;

    @JsonDetails
    protected double regionalDifficultyFactor;

    @JsonDetails
    protected double regionalDifficultyPercentage;


    public CachedChunk(Chunk chunk) {
        super(chunk);

        this.uuid = UUID.fromString(chunk.getUniqueId().toString());
        this.pos = chunk.getPosition().clone();
        this.world = WebAPI.getCacheService().getWorld(chunk.getWorld());
        this.blockMin = chunk.getBlockMin().clone();
        this.blockMax = chunk.getBlockMax().clone();
        this.loaded = chunk.isLoaded();
        this.inhabitedTime = chunk.getInhabittedTime();
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
        return "/api/world/" + world.getUUID() + "/" + pos.getX() + "/" + pos.getZ();
    }
}
