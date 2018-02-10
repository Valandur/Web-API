package valandur.webapi.api.block;

import com.flowpowered.math.vector.Vector3i;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.event.cause.Cause;
import valandur.webapi.api.cache.ICachedObject;

import java.util.UUID;

/**
 * A block operation is a running operation on the world which performs a certain action to blocks. This operation is
 * throttled over time so that it does not slow down the minecraft server too much.
 */
@ApiModel("BlockOperation")
public interface IBlockOperation extends ICachedObject<IBlockOperation> {

    /**
     * The type of block operation
     */
    enum BlockOperationType {
        GET, CHANGE
    }

    /**
     * The current status of the update.
     */
    enum BlockOperationStatus {
        INIT, RUNNING, PAUSED, DONE, ERRORED, CANCELED,
    }

    /**
     * Gets the UUID that uniquely identifies this block update operation.
     * @return The UUID of this operation.
     */
    @ApiModelProperty(value = "The unique UUID identifying this block operation", required = true)
    UUID getUUID();

    /**
     * Gets the type of block operation.
     * @return The type of block operation.
     */
    @ApiModelProperty(value = "The type of block operation", required = true)
    BlockOperationType getType();

    /**
     * Gets the current status of this operation.
     * @return The status of this block update operation.
     */
    @ApiModelProperty(value = "The current status of the block operation", required = true)
    BlockOperationStatus getStatus();

    /**
     * Gets the minimum block belonging to this operation.
     * @return The minimum block belonging to this operation.
     */
    @ApiModelProperty("The minimum block belonging to this operation")
    Vector3i getMin();

    /**
     * Gets the maximum block belonging to this operation.
     * @return The maximum block belonging to this operation.
     */
    @ApiModelProperty("The maximum block belonging to this operation")
    Vector3i getMax();

    /**
     * True if this operation resulted in an error, possibly because of an unloaded world or an out-of-bounds error.
     * If this is true then {@link #getStatus()} will return the state {@link BlockOperationStatus#ERRORED} and
     * {@link #getError()} will return an error message.
     * @return True if an error occured, false otherwise.
     */
    @ApiModelProperty("True if this block operation produced errors, false otherwise.")
    boolean isErrored();

    /**
     * Gets any error message that occurred while running this block operation.
     * @return The error that occurred for this block operation, or null if no error occurred.
     */
    @ApiModelProperty("The error message, if any")
    String getError();

    /**
     * Gets the cause which is used for the block operation.
     * @return The cause used for the block operation.
     */
    @ApiModelProperty("The cause which is used for updating the blocks")
    Cause getCause();

    /**
     * Gets the current progress of this operation as a value between 0 and 1.0
     * @return The current progress, between 0 and 1.
     */
    @ApiModelProperty("The current progress of the block operation, from 0 (=started) to 1 (=finished)")
    float getProgress();

    /**
     * Gets the estimated amount of time in seconds that this operation will run for.
     * @return The estimated amount of time remaining until this operation is done.
     */
    @ApiModelProperty("The estimated amount of time remaining until this block operation is complete (in seconds)")
    float getEstimatedSecondsRemaining();

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
