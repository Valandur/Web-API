package io.valandur.webapi.fabric.command;

import io.valandur.webapi.command.CommandService;
import io.valandur.webapi.fabric.FabricWebAPI;

public class FabricCommandService extends CommandService<FabricWebAPI> {
    public FabricCommandService(FabricWebAPI webapi) {
        super(webapi);
    }

    @Override
    public void executeCommand(String cmd) {

    }
}
