package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.CommandData;
import org.spongepowered.api.text.Text;
import valandur.webapi.serialize.BaseView;

@ApiModel("CommandData")
public class CommandDataView extends BaseView<CommandData> {

    @ApiModelProperty(value = "True if the output is tracked, false otherwise", required = true)
    public boolean tracksOutput;

    @ApiModelProperty("The last output produced")
    public Text lastOutput;

    @ApiModelProperty(value = "The stored command", required = true)
    public String storedCommand;

    @ApiModelProperty(value = "The amount of successfull executions", required = true)
    public int successCount;


    public CommandDataView(CommandData value) {
        super(value);

        this.tracksOutput = value.doesTrackOutput().get();
        this.lastOutput = value.lastOutput().get().orElse(null);
        this.storedCommand = value.storedCommand().get();
        this.successCount = value.successCount().get();
    }
}
