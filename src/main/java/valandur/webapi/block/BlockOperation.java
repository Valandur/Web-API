package valandur.webapi.block;

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

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class BlockOperation implements IBlockOperation {

    private IBlockService blockService;

    private int currentBlock = 0;
    private int blocksSet = 0;
    public int getBlocksProcessed() {
        return blocksSet;
    }

    protected BlockOperationStatus status = BlockOperationStatus.INIT;
    public BlockOperationStatus getStatus() {
        return status;
    }

    protected String error = null;
    public boolean hasError() {
        return error != null;
    }
    public String getError() {
        return error;
    }

    protected UUID uuid;
    public UUID getUUID() {
        return uuid;
    }

    protected Cause cause;
    public Cause getCause() {
        return cause;
    }

    private Task task;

    protected ICachedWorld world;
    protected final Vector3i min;
    protected final Vector3i max;
    protected final Vector3i size;
    private final int totalBlocks;


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

    final public float getProgress() {
        return (float)currentBlock / totalBlocks;
    }

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
        for (int i = currentBlock; i < nextLimit; i++) {
            int x = currentBlock / (size.getY() * size.getZ());
            int y = currentBlock - x * size.getY() * size.getZ();
            int z = currentBlock - y * size.getY();
            Vector3i pos = new Vector3i(x, y, z);

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

            boolean set = processBlock(world, pos);
            if (set) {
                blocksSet++;
            }
        }

        currentBlock = nextLimit;
        if (currentBlock >= totalBlocks - 1) {
            stop(null);
        }

        Sponge.getEventManager().post(new BlockOperationProgressEvent(this));
    }
    protected abstract boolean processBlock(World world, Vector3i pos);

    final public void pause() {
        if (status != BlockOperationStatus.RUNNING) return;

        task.cancel();
        status = BlockOperationStatus.PAUSED;

        Sponge.getEventManager().post(new BlockOperationStatusChangeEvent(this));
    }

    final public void stop(String error) {
        if (status != BlockOperationStatus.RUNNING && status != BlockOperationStatus.PAUSED) return;

        task.cancel();
        status = error == null ? BlockOperationStatus.DONE : BlockOperationStatus.ERRORED;
        this.error = error;

        Sponge.getEventManager().post(new BlockOperationStatusChangeEvent(this));
    }
}
