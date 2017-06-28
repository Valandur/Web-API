package valandur.webapi.api.block;

import org.spongepowered.api.event.cause.Cause;

import java.util.UUID;

public interface IBlockUpdate {

    enum BlockUpdateStatus {
        INIT, RUNNING, PAUSED, DONE, ERRORED,
    }

    int getBlocksSet();

    boolean hasError();

    BlockUpdateStatus getStatus();
    String getError();
    UUID getUUID();
    Cause getCause();
    float getProgress();

    void start();
    void pause();
    void stop(String error);
}
