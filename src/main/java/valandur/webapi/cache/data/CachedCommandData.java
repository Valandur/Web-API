package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.CommandData;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedText;

@ApiModel("CommandData")
public class CachedCommandData extends CachedObject<CommandData> {

    @ApiModelProperty(value = "True if the output is tracked, false otherwise", required = true)
    public boolean tracksOutput;

    @ApiModelProperty("The last output produced")
    public CachedText lastOutput;

    @ApiModelProperty(value = "The stored command", required = true)
    public String storedCommand;

    @ApiModelProperty(value = "The amount of successfull executions", required = true)
    public int successCount;


    public CachedCommandData(CommandData value) {
        super(value);

        this.tracksOutput = value.doesTrackOutput().get();
        this.lastOutput = value.lastOutput().get().map(CachedText::new).orElse(null);
        this.storedCommand = value.storedCommand().get();
        this.successCount = value.successCount().get();
    }
}
