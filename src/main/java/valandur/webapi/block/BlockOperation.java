package valandur.webapi.block;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.api.block.IBlockOperation;
import valandur.webapi.api.block.IBlockService;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.serialize.JsonDetails;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class BlockOperation implements IBlockOperation {

    private IBlockService blockService;
    private final int totalBlocks;
    private int currentBlock = 0;
    private Task task;

    protected BlockOperationStatus status = BlockOperationStatus.INIT;
    protected UUID uuid;
    protected String error = null;
    protected Cause cause;
    protected ICachedWorld world;
    protected final Vector3i min;
    protected final Vector3i max;
    protected final Vector3i size;

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public BlockOperationStatus getStatus() {
        return status;
    }

    @Override
    @JsonDetails
    public Vector3i getMin() {
        return min;
    }

    @Override
    @JsonDetails
    public Vector3i getMax() {
        return max;
    }

    @Override
    @JsonIgnore
    public boolean hasError() {
        return error != null;
    }

    @Override
    @JsonDetails
    public String getError() {
        return error;
    }

    @Override
    @JsonIgnore
    public Cause getCause() {
        return cause;
    }


    public BlockOperation(ICachedWorld world, Vector3i min, Vector3i max) {
        this.blockService = WebAPI.getBlockService();
        this.uuid = UUID.randomUUID();
        this.world = world;
        this.min = min;
        this.max = max;
        this.size = max.sub(min).add(1, 1, 1);
        this.cause = Cause.source(WebAPI.getContainer()).build();
        this.totalBlocks = size.getX() * size.getY() * size.getZ();
    }

    @Override
    final public float getProgress() {
        return (float)currentBlock / totalBlocks;
    }
    @Override
    public float getEstimatedSecondsRemaining() {
        return (float)(totalBlocks - currentBlock) / blockService.getMaxBlocksPerSecond();
    }

    @Override
    final public void start() {
        if (status != BlockOperationStatus.INIT && status != BlockOperationStatus.PAUSED) return;
        status = BlockOperationStatus.RUNNING;

        task = Task.builder()
                .execute(this::run)
                .name("WebAPI - BlockUpdate - " + this.getUUID().toString())
                .interval(500, TimeUnit.MILLISECONDS)
                .submit(WebAPI.getInstance());

        Sponge.getEventManager().post(new BlockOperationStatusChangeEvent(this));
    }

    private void run() {
        if (status != BlockOperationStatus.RUNNING) return;

        Optional<?> optWorld = world.getLive();
        if (!optWorld.isPresent()) {
            stop("Invalid world");
            return;
        }

        World world = (World)optWorld.get();

        int nextLimit = Math.min(currentBlock + blockService.getMaxBlocksPerSecond() / 2, totalBlocks);
        for (; currentBlock < nextLimit; currentBlock++) {
            int x = currentBlock / (size.getY() * size.getZ());
            int y = (currentBlock - x * size.getY() * size.getZ()) / size.getZ();
            int z = currentBlock - x * size.getY() * size.getZ() - y * size.getZ();
            Vector3i pos = new Vector3i(x, y, z).add(min);

            Optional<Vector3i> chunkPos = Sponge.getServer().getChunkLayout().toChunk(pos);
            if (!chunkPos.isPresent()) {
                stop("Invalid chunk pos");
                return;
            }

            Optional<Chunk> chunk = world.loadChunk(chunkPos.get(), true);
            if (!chunk.isPresent()) {
                stop("Invalid chunk");
                return;
            }

            processBlock(world, pos);
        }

        if (currentBlock >= totalBlocks - 1) {
            stop(null);
        }

        Sponge.getEventManager().post(new BlockOperationProgressEvent(this));
    }
    protected abstract void processBlock(World world, Vector3i pos);

    @Override
    final public void pause() {
        if (status != BlockOperationStatus.RUNNING) return;

        task.cancel();
        status = BlockOperationStatus.PAUSED;

        Sponge.getEventManager().post(new BlockOperationStatusChangeEvent(this));
    }

    @Override
    final public void stop(String error) {
        if (status != BlockOperationStatus.RUNNING && status != BlockOperationStatus.PAUSED) return;

        boolean res = task.cancel();
        status = error == null ? BlockOperationStatus.DONE : BlockOperationStatus.ERRORED;
        this.error = error;

        Sponge.getEventManager().post(new BlockOperationStatusChangeEvent(this));
    }
}
