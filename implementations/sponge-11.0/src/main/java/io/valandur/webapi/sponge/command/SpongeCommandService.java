package io.valandur.webapi.sponge.command;

import io.valandur.webapi.command.CommandService;
import io.valandur.webapi.sponge.SpongeWebAPI;

public class SpongeCommandService extends CommandService<SpongeWebAPI> {
    public SpongeCommandService(SpongeWebAPI webapi) {
        super(webapi);
    }

    @Override
    public void executeCommand(String cmd) {

    }
}
