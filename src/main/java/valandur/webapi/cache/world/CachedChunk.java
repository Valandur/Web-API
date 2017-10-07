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

    private UUID uuid;
    public UUID getUUID() {
        return uuid;
    }

    private Vector3i pos;
    public Vector3i getPosition() {
        return pos;
    }

    @JsonDetails(simple = true)
    private ICachedWorld world;
    public ICachedWorld getWorld() {
        return world;
    }

    private boolean loaded;
    public boolean isLoaded() {
        return loaded;
    }

    private Vector3i blockMin;
    @JsonDetails
    public Vector3i getBlockMin() {
        return blockMin;
    }

    private Vector3i blockMax;
    @JsonDetails
    public Vector3i getBlockMax() {
        return blockMax;
    }

    private int inhabitedTime;
    @JsonDetails
    public int getInhabitedTime() {
        return inhabitedTime;
    }

    private double regionalDifficultyFactor;
    @JsonDetails
    public double getRegionalDifficultyFactor() {
        return regionalDifficultyFactor;
    }

    private double regionalDifficultyPercentage;
    @JsonDetails
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
