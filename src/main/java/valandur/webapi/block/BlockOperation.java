package valandur.webapi.block;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.flowpowered.math.vector.Vector3i;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.serialize.JsonDetails;
import valandur.webapi.util.Constants;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include =  JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = BlockOperation.class,
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BlockGetOperation.class, name = "GET"),
        @JsonSubTypes.Type(value = BlockChangeOperation.class, name = "CHANGE"),
})
@ApiModel(value = "BlockOperation", subTypes = { BlockGetOperation.class, BlockChangeOperation.class })
public abstract class BlockOperation extends CachedObject<BlockOperation> {

    /**
     * The type of block operation
     */
    public enum BlockOperationType {
        GET, CHANGE
    }

    /**
     * The current status of the update.
     */
    public enum BlockOperationStatus {
        INIT, RUNNING, PAUSED, DONE, ERRORED, CANCELED,
    }


    private BlockService blockService;
    private final int totalBlocks;
    private int currentBlock = 0;
    private Task task;

    protected BlockOperationStatus status = BlockOperationStatus.INIT;
    protected UUID uuid;
    protected String error = null;
    protected Cause cause;
    protected CachedWorld world;
    protected final Vector3i min;
    protected final Vector3i max;
    protected final Vector3i size;

    @ApiModelProperty(value = "The type of block operation", required = true)
    public abstract BlockOperationType getType();

    @ApiModelProperty(value = "The unique UUID identifying this block operation", required = true)
    public UUID getUUID() {
        return uuid;
    }

    @ApiModelProperty(value = "The current status of the block operation", required = true)
    public BlockOperationStatus getStatus() {
        return status;
    }

    @JsonDetails
    @ApiModelProperty(value = "The world in which this block operation is running", required = true)
    public CachedWorld getWorld() {
        return world;
    }

    @JsonDetails
    @ApiModelProperty(value = "The minimum block belonging to this operation", required = true)
    public Vector3i getMin() {
        return min;
    }

    @JsonDetails
    @ApiModelProperty(value = "The maximum block belonging to this operation", required = true)
    public Vector3i getMax() {
        return max;
    }

    @JsonIgnore
    @ApiModelProperty(value = "True if this block operation produced errors, false otherwise.", required = true)
    public boolean isErrored() {
        return error != null;
    }

    @JsonDetails
    @ApiModelProperty(value = "The error message, if any", required = true)
    public String getError() {
        return error;
    }

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public Cause getCause() {
        return cause;
    }


    public BlockOperation(CachedWorld world, Vector3i min, Vector3i max) {
        super(null);

        this.blockService = WebAPI.getBlockService();
        this.uuid = UUID.randomUUID();
        this.world = world;
        this.min = min;
        this.max = max;
        this.size = max.sub(min).add(1, 1, 1);
        this.cause = Cause.source(WebAPI.getContainer()).build();
        this.totalBlocks = size.getX() * size.getY() * size.getZ();
    }

    @ApiModelProperty(
            value = "The current progress of the block operation, from 0 (=started) to 1 (=finished)",
            required = true)
    final public float getProgress() {
        return (float)currentBlock / totalBlocks;
    }

    @ApiModelProperty(
            value = "The estimated amount of time remaining until this block operation is complete (in seconds)",
            required = true)
    public float getEstimatedSecondsRemaining() {
        return (float)(totalBlocks - currentBlock) / blockService.getMaxBlocksPerSecond();
    }

    /**
     * Starts this operation. Has no effect if already started, done or errored.
     */
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

    /**
     * Pauses this operation. Has no effect if not running.
     */
    final public void pause() {
        if (status != BlockOperationStatus.RUNNING) return;

        task.cancel();
        status = BlockOperationStatus.PAUSED;

        Sponge.getEventManager().post(new BlockOperationStatusChangeEvent(this));
    }

    /**
     * Stops this operation. Has no effect if not running or paused.
     * Once an operation is stopped it cannot be resumed.
     * @param error The error, if any, that occurred and is the reason this operation was stopped.
     */
    final public void stop(String error) {
        if (status != BlockOperationStatus.RUNNING && status != BlockOperationStatus.PAUSED) return;

        boolean res = task.cancel();
        if (error == null || error.isEmpty()) {
            this.status = currentBlock >= totalBlocks - 1 ? BlockOperationStatus.DONE : BlockOperationStatus.CANCELED;
            this.error = null;
        } else {
            this.status = BlockOperationStatus.ERRORED;
            this.error = error;
        }

        Sponge.getEventManager().post(new BlockOperationStatusChangeEvent(this));
    }

    public String getLink() {
        return Constants.BASE_PATH + "/block/op/" + uuid;
    }

    public Optional<BlockOperation> getLive() {
        return WebAPI.getBlockService().getBlockOperation(uuid);
    }
}
