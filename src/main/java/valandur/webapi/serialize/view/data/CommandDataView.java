package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.CommandData;
import org.spongepowered.api.text.Text;
import valandur.webapi.api.serialize.BaseView;

public class CommandDataView extends BaseView<CommandData> {

    public boolean tracksOutput;
    public Text lastOutput;
    public String storedCommand;
    public int successCount;


    public CommandDataView(CommandData value) {
        super(value);

        this.tracksOutput = value.doesTrackOutput().get();
        this.lastOutput = value.lastOutput().get().orElse(null);
        this.storedCommand = value.storedCommand().get();
        this.successCount = value.successCount().get();
    }
}
