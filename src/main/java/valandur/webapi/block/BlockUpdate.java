package valandur.webapi.block;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BlockUpdate {
    public static int MAX_BLOCKS_PER_SECOND = 10000;

    public enum BlockUpdateStatus {
        INIT, RUNNING, PAUSED, DONE, ERRORED,
    }

    //private int currentChunk = 0;
    private int currentBlock = 0;
    private int blocksSet = 0;
    public int getBlocksSet() {
        return blocksSet;
    }

    private BlockUpdateStatus status = BlockUpdateStatus.INIT;
    public BlockUpdateStatus getStatus() {
        return status;
    }

    private String error = null;
    public boolean hasError() {
        return error != null;
    }
    public String getError() {
        return error;
    }

    private UUID uuid;
    public UUID getUUID() {
        return uuid;
    }

    private Cause cause;
    public Cause getCause() {
        return cause;
    }

    private Task task;
    private UUID worldId;
    private List<Tuple<Vector3i, BlockState>> blocks;


    public float getProgress() {
        return (float)currentBlock / blocks.size();
    }

    public BlockUpdate(UUID worldId, List<Tuple<Vector3i, BlockState>> blocks) {
        this.uuid = UUID.randomUUID();
        this.worldId = worldId;
        this.blocks = blocks;
        this.cause = Cause.source(WebAPI.getInstance().getContainer()).build();
    }

    public void start() {
        if (status != BlockUpdateStatus.INIT && status != BlockUpdateStatus.PAUSED) return;
        status = BlockUpdateStatus.RUNNING;

        task = Task.builder()
                .execute(this::run)
                .name("WebAPI - BlockUpdate - " + this.getUUID().toString())
                .interval(500, TimeUnit.MILLISECONDS)
                .submit(WebAPI.getInstance());

        Sponge.getEventManager().post(new BlockUpdateStatusChangeEvent(this));
    }

    private void run() {
        if (status != BlockUpdateStatus.RUNNING) return;

        Optional<World> world = Sponge.getServer().getWorld(worldId);
        if (!world.isPresent()) {
            stop("Invalid world");
            return;
        }

        int nextLimit = Math.min(currentBlock + MAX_BLOCKS_PER_SECOND / 2, blocks.size());
        World w = world.get();

        for (int i = currentBlock; i < nextLimit; i++) {
            Tuple<Vector3i, BlockState> block = blocks.get(i);

            Optional<Vector3i> chunkPos = Sponge.getServer().getChunkLayout().toChunk(block.getFirst());
            if (!chunkPos.isPresent()) {
                stop("Invalid chunk pos");
                return;
            }

            Optional<Chunk> chunk = w.loadChunk(chunkPos.get(), true);
            if (!chunk.isPresent()) {
                stop("Invalid chunk");
                return;
            }

            boolean set = w.setBlock(blocks.get(i).getFirst(), blocks.get(i).getSecond(), cause);
            if (set) {
                blocksSet++;
            }
        }

        currentBlock = nextLimit;
        if (currentBlock >= blocks.size() - 1) {
            stop(null);
        }

        Sponge.getEventManager().post(new BlockUpdateProgressEvent(this));
    }

    public void pause() {
        if (status != BlockUpdateStatus.RUNNING) return;

        task.cancel();
        status = BlockUpdateStatus.PAUSED;

        Sponge.getEventManager().post(new BlockUpdateStatusChangeEvent(this));
    }

    public void stop(String error) {
        if (status != BlockUpdateStatus.RUNNING && status != BlockUpdateStatus.PAUSED) return;

        task.cancel();
        status = error == null ? BlockUpdateStatus.DONE : BlockUpdateStatus.ERRORED;
        this.error = error;

        Sponge.getEventManager().post(new BlockUpdateStatusChangeEvent(this));
    }
}
