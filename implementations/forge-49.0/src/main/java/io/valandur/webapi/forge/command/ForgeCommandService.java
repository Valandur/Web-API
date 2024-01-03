package io.valandur.webapi.forge.command;

import io.valandur.webapi.command.CommandService;
import io.valandur.webapi.forge.ForgeWebAPI;

public class ForgeCommandService extends CommandService<ForgeWebAPI> {

    public ForgeCommandService(ForgeWebAPI webapi) {
        super(webapi);
    }

    @Override
    public void executeCommand(String cmd) {

    }
}
