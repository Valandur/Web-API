package valandur.webapi.cache.world;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.Chunk;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.world.ICachedChunk;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.cache.CachedObject;

import java.util.UUID;

public class CachedChunk extends CachedObject implements ICachedChunk {

    private UUID uuid;
    @Override
    public UUID getUUID() {
        return null;
    }

    private Vector3i pos;
    @Override
    public Vector3i getPosition() {
        return null;
    }

    private ICachedWorld world;
    @Override
    public ICachedWorld getWorld() {
        return null;
    }

    private Vector3i blockMin;
    @Override
    public Vector3i getBlockMin() {
        return null;
    }

    private Vector3i blockMax;
    @Override
    public Vector3i getBlockMax() {
        return null;
    }

    private boolean isLoaded;
    @Override
    public boolean isLoaded() {
        return false;
    }

    private int inhabitedTime;
    @Override
    public int getInhabittedTime() {
        return 0;
    }

    private double regionalDifficultyFactor;
    @Override
    public double getRegionalDifficultyFactor() {
        return 0;
    }

    private double regionalDifficultyPercentage;
    @Override
    public double getRegionalDifficultyPercentage() {
        return 0;
    }


    public CachedChunk(Chunk chunk) {
        super(chunk);

        this.uuid = chunk.getUniqueId();
        this.pos = chunk.getPosition();
        this.world = WebAPI.getCacheService().getWorld(chunk.getWorld());
        this.blockMin = chunk.getBlockMin();
        this.blockMax = chunk.getBlockMax();
        this.isLoaded = chunk.isLoaded();
        this.inhabitedTime = chunk.getInhabittedTime();
        this.regionalDifficultyFactor = chunk.getRegionalDifficultyFactor();
        this.regionalDifficultyPercentage = chunk.getRegionalDifficultyPercentage();
    }
}
