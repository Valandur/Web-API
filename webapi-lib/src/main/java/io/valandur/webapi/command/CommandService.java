package io.valandur.webapi.command;

import io.valandur.webapi.Service;
import io.valandur.webapi.WebAPI;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandService<T extends WebAPI<?, ?>> extends Service<T> {

    protected List<CommandHistoryItem> commandHistory;

    public CommandService(T webapi) {
        super(webapi);

        commandHistory = new ArrayList<>();
    }

    public List<CommandHistoryItem> getCommandHistory() {
        return commandHistory;
    }

    public abstract void executeCommand(String cmd);
}
