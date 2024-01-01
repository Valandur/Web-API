package io.valandur.webapi.spigot.command;

import io.valandur.webapi.command.CommandService;
import io.valandur.webapi.spigot.SpigotWebAPI;

public class SpigotCommandService extends CommandService<SpigotWebAPI> {
    public SpigotCommandService(SpigotWebAPI webapi) {
        super(webapi);
    }

    @Override
    public void executeCommand(String cmd) {

    }
}
