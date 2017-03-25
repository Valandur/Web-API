package valandur.webapi.command;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import valandur.webapi.WebAPI;
import valandur.webapi.hooks.WebHook;
import valandur.webapi.hooks.WebHookParam;
import valandur.webapi.hooks.WebHooks;

import java.util.*;

public class CommandRegistry {
    public static void init() {
        CommandManager manager = Sponge.getCommandManager();
        Logger logger = WebAPI.getInstance().getLogger();

        // Register commands
        logger.info("Registering commands...");

        // Whitelist
        CommandSpec specWhitelistAdd = CommandSpec.builder()
                .description(Text.of("Add an IP to the whitelist"))
                .permission("webapi.command.whitelist.add")
                .arguments(new CmdIpElement(Text.of("ip")))
                .executor(new CmdAuthListAdd(true))
                .build();
        CommandSpec specWhitelistRemove = CommandSpec.builder()
                .description(Text.of("Remove an IP from the whitelist"))
                .permission("webapi.command.whitelist.remove")
                .arguments(new CmdIpElement(Text.of("ip")))
                .executor(new CmdAuthListRemove(true))
                .build();
        CommandSpec specWhitelistEnable = CommandSpec.builder()
                .description(Text.of("Enable the whitelist"))
                .permission("webapi.command.whitelist.enable")
                .executor(new CmdAuthListEnable(true))
                .build();
        CommandSpec specWhitelistDisable = CommandSpec.builder()
                .description(Text.of("Disable the whitelist"))
                .permission("webapi.command.whitelist.disable")
                .executor(new CmdAuthListDisable(true))
                .build();
        CommandSpec specWhitelist = CommandSpec.builder()
                .description(Text.of("Manage the whitelist"))
                .permission("webapi.command.whitelist")
                .child(specWhitelistAdd, "add")
                .child(specWhitelistRemove, "remove")
                .child(specWhitelistEnable, "enable")
                .child(specWhitelistDisable, "disable")
                .build();

        // Blacklist
        CommandSpec specBlacklistAdd = CommandSpec.builder()
                .description(Text.of("Add an IP to the blacklist"))
                .permission("webapi.command.blacklist.add")
                .arguments(GenericArguments.string(Text.of("ip")))
                .executor(new CmdAuthListAdd(false))
                .build();
        CommandSpec specBlaclistRemove = CommandSpec.builder()
                .description(Text.of("Remove an IP from the blacklist"))
                .permission("webapi.command.blacklist.remove")
                .arguments(GenericArguments.string(Text.of("ip")))
                .executor(new CmdAuthListRemove(false))
                .build();
        CommandSpec specBlacklistEnable = CommandSpec.builder()
                .description(Text.of("Enable the blacklist"))
                .permission("webapi.command.blacklist.enable")
                .executor(new CmdAuthListEnable(false))
                .build();
        CommandSpec specBlacklistDisable = CommandSpec.builder()
                .description(Text.of("Disable the blacklist"))
                .permission("webapi.command.blacklist.disable")
                .executor(new CmdAuthListDisable(false))
                .build();
        CommandSpec specBlacklist = CommandSpec.builder()
                .description(Text.of("Manage the blacklist"))
                .permission("webapi.command.blacklist")
                .child(specBlacklistAdd, "add")
                .child(specBlaclistRemove, "remove")
                .child(specBlacklistEnable, "enable")
                .child(specBlacklistDisable, "disable")
                .build();

        // Notify commands
        Map<List<String>, CommandSpec> hookSpecs = new HashMap<>();
        Map<List<String>, CommandSpec> hookAliases = new HashMap<>();
        for (WebHook hook : WebHooks.getCommandHooks().values()) {
            List<CommandElement> args = new ArrayList<>();

            if (hook.getParams() != null) {
                for (WebHookParam param : hook.getParams()) {
                    Optional<CommandElement> e = param.getCommandElement();
                    e.ifPresent(args::add);
                }
            }

            CommandSpec hookCmd = CommandSpec.builder()
                    .description(Text.of("Notify the " + hook.getName() + " hook"))
                    .permission("webapi.command.notify." + hook.getName())
                    .arguments(args.toArray(new CommandElement[args.size()]))
                    .executor(new CmdNotifyHook(hook))
                    .build();
            if (hook.getAliases() != null && hook.getAliases().size() > 0) hookAliases.put(hook.getAliases(), hookCmd);
            hookSpecs.put(Collections.singletonList(hook.getName()), hookCmd);
        }

        // Notify parent
        CommandSpec specNotifyHook = CommandSpec.builder()
                .description(Text.of("Notify a hook"))
                .permission("webapi.command.notify")
                .children(hookSpecs)
                .build();

        // Register main command
        CommandSpec spec = CommandSpec.builder()
                .description(Text.of("Manage Web-API settings"))
                .permission("webapi.command")
                .child(specWhitelist, "whitelist")
                .child(specBlacklist, "blacklist")
                .child(specNotifyHook, "notify")
                .build();
        manager.register(WebAPI.getInstance(), spec, "webapi");

        // Register aliases for notify commands
        for (Map.Entry<List<String>, CommandSpec> entry : hookAliases.entrySet()) {
            manager.register(WebAPI.getInstance(), entry.getValue(), entry.getKey());
        }
    }
}
