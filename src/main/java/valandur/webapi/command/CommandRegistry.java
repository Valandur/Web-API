package valandur.webapi.command;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import valandur.webapi.WebAPI;

public class CommandRegistry {
    public static void init() {
        Logger logger = WebAPI.getInstance().getLogger();

        // Register commands
        logger.info("Registering commands...");

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

        CommandSpec spec = CommandSpec.builder()
                .description(Text.of("Manage Web-API settings"))
                .permission("webapi.command")
                .child(specWhitelist, "whitelist")
                .child(specBlacklist, "blacklist")
                .build();
        Sponge.getCommandManager().register(WebAPI.getInstance(), spec, "webapi");
    }
}
