package valandur.webapi.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.hooks.WebHook;
import valandur.webapi.hooks.WebHookParam;
import valandur.webapi.hooks.WebHooks;
import valandur.webapi.json.JsonConverter;

import java.util.*;

public class CmdNotifyHook implements CommandExecutor {

    private WebHook hook;

    public CmdNotifyHook(WebHook hook) {
        this.hook = hook;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Map<String, Tuple<String, JsonNode>> params = new LinkedHashMap<>();
        for (WebHookParam param : hook.getParams()) {
            params.put(param.getName(), param.getValue(args).orElse(new Tuple<>("null", NullNode.getInstance())));
        }

        WebHooks.notifyHook(hook, src.getIdentifier(), params);

        return CommandResult.success();
    }
}
