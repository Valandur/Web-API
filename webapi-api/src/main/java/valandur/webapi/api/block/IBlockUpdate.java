package valandur.webapi.api.block;

import org.spongepowered.api.event.cause.Cause;

import java.util.UUID;

/**
 * A block update is a running operation on the world which changes certain blocks. This operation is throttled over
 * time so that it does not slow down the minecraft server too much.
 */
public interface IBlockUpdate {

    /**
     * The current status of the update.
     */
    enum BlockUpdateStatus {
        INIT, RUNNING, PAUSED, DONE, ERRORED,
    }

    /**
     * Gets the number of blocks that have been changed. Blocks that already match what this update would change them
     * to are not counted here.
     * @return The number of changed blocks.
     */
    int getBlocksSet();

    /**
     * True if this operation resulted in an error, possibly because of an unloaded world or an out-of-bounds error.
     * If this is true then {@link #getStatus()} will return the state {@link BlockUpdateStatus#ERRORED} and
     * {@link #getError()} will return an error message.
     * @return True if an error occured, false otherwise.
     */
    boolean hasError();

    /**
     * Gets any error message that occurred while running this block operation.
     * @return The error that occurred for this block operation, or null if no error occurred.
     */
    String getError();

    /**
     * Gets the current status of this operation.
     * @return The status of this block update operation.
     */
    BlockUpdateStatus getStatus();

    /**
     * Gets the UUID that uniquely identifies this block update operation.
     * @return The UUID of this operation.
     */
    UUID getUUID();

    /**
     * Gets the cause which is used for the block updates.
     * @return The cause used for the block updates.
     */
    Cause getCause();

    /**
     * Gets the current progress of this operation as a value between 0 and 1.0
     * @return The current progress, between 0 and 1.
     */
    float getProgress();

    /**
     * Starts this operation. Has no effect if already started, done or errored.
     */
    void start();

    /**
     * Pauses this operation. Has no effect if not running.
     */
    void pause();

    /**
     * Stops this operation. Has no effect if not running or paused.
     * Once an operation is stopped it cannot be resumed.
     * @param error The error, if any, that occurred and is the reason this operation was stopped.
     */
    void stop(String error);
}
