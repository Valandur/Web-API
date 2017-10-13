package valandur.webapi.serialize.view.misc;

import org.spongepowered.api.command.CommandSource;
import valandur.webapi.api.serialize.BaseView;

public class CommandSourceView extends BaseView<CommandSource> {

    public String id;
    public String name;


    public CommandSourceView(CommandSource value) {
        super(value);

        this.id = value.getIdentifier();
        this.name = value.getName();
    }
}
