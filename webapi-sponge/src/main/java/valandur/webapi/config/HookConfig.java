package valandur.webapi.config;

import com.google.common.collect.Lists;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import valandur.webapi.hook.CommandWebHook;
import valandur.webapi.hook.WebHook;
import valandur.webapi.hook.WebHookHeader;
import valandur.webapi.hook.WebHookParam;
import valandur.webapi.hook.WebHookService.WebHookType;
import valandur.webapi.security.SecurityService;

import javax.ws.rs.HttpMethod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class HookConfig extends BaseConfig {

    private static Map<String, CommandWebHook> defaultCommandHooks() {
        Map<String, CommandWebHook> hooks = new HashMap<>();
        hooks.put("test", new CommandWebHook(
                false,
                Lists.newArrayList("test"),
                Lists.newArrayList(
                        new WebHookParam("player", WebHookParam.WebHookParamType.PLAYER, false),
                        new WebHookParam("world", WebHookParam.WebHookParamType.WORLD, true)
                ),
                Lists.newArrayList(
                        new WebHook(
                                "http://localhost/test/{player}",
                                false,
                                HttpMethod.POST,
                                WebHook.WebHookDataType.JSON,
                                false,
                                Lists.newArrayList(new WebHookHeader("X-SUPER-SECRET", "my_secret_to_verify")),
                                true,
                                SecurityService.permitAllNode()
                        )
                )
        ));
        return hooks;
    }

    @Setting(comment = "The following are event hooks which are all called when the specified event happens. They get passed various data\n" +
            "in the body (if method != GET, described below for each event). The format of the hooks is described in the \"all\"\n" +
            "event hook. This format can be used for any of the hook lists.\n" +
            "Please note that the hooks don't get called in any particular order, and might even be called concurrently.")
    public HookEvents events = new HookEvents();

    @Setting(comment = "These hooks can be used to bind to custom events. You have to use the fully qualified class name as the key, and then\n" +
            "a hook list just like for the event hooks. These hooks work just like the event hooks, although the data\n" +
            "depends on the event you subscribe to.")
    public Map<String, List<WebHook>> custom = new HashMap<>();

    @Setting(comment = "The endpoints here get called when executing the specified command. " +
            "For example the hook \"test\" would get executed\n" +
            "when running the command /webapi notify test")
    public Map<String, CommandWebHook> command = defaultCommandHooks();


    @ConfigSerializable
    public static class HookEvents {
        @Setting(comment = "This event is fired for all events. The \"X-WebAPI-Event\" header specifies the exact event.")
        public List<WebHook> all = Lists.newArrayList(new WebHook(
                "http://localhost/test",
                false,
                HttpMethod.POST,
                WebHook.WebHookDataType.JSON,
                false,
                Lists.newArrayList(new WebHookHeader("X-SUPER-SECRET", "my_secret_to_verify")),
                true,
                SecurityService.permitAllNode()
        ));

        @Setting(comment = "This event is fired when a player earns an achievement / advancement")
        public List<WebHook> achievement = new ArrayList<>();

        @Setting(comment = "This event is fired when a block operation changes status")
        public List<WebHook> block_operation_status = new ArrayList<>();

        @Setting(comment = "This event is fired when a chat message is sent on the server")
        public List<WebHook> chat = new ArrayList<>();

        @Setting(comment = "This event is fired when a command is executed")
        public List<WebHook> command = new ArrayList<>();

        @Setting(comment = "This event is fired when a player clicks one of the options of an interactive message")
        public List<WebHook> interactive_message = new ArrayList<>();

        @Setting(comment = "This event is fired when a chunk is generated")
        public List<WebHook> generate_chunk = new ArrayList<>();

        @Setting(comment = "This event is fired when an explosion occurs")
        public List<WebHook> explosion = new ArrayList<>();

        @Setting(comment = "This event is fired when an entity spawns")
        public List<WebHook> entity_spawn = new ArrayList<>();

        @Setting(comment = "This event is fired when an entity despawns")
        public List<WebHook> entity_despawn = new ArrayList<>();

        @Setting(comment = "This event is fired when an entity expires")
        public List<WebHook> entity_expire = new ArrayList<>();

        @Setting(comment = "This event is fired when a player interacts with a block")
        public List<WebHook> interact_block = new ArrayList<>();

        @Setting(comment = "This event is fired when an inventory is opened")
        public List<WebHook> inventory_open = new ArrayList<>();

        @Setting(comment = "This event is fired when an inventory is closed")
        public List<WebHook> inventory_close = new ArrayList<>();

        @Setting(comment = "This event is fired when a player joins the server")
        public List<WebHook> player_join = new ArrayList<>();

        @Setting(comment = "This event is fired when a player leaves the server")
        public List<WebHook> player_leave = new ArrayList<>();

        @Setting(comment = "This event is fired when a player dies")
        public List<WebHook> player_death = new ArrayList<>();

        @Setting(comment = "This event is fired when a player gets kicked")
        public List<WebHook> player_kick = new ArrayList<>();

        @Setting(comment = "This event is fired when a player gets banned")
        public List<WebHook> player_ban = new ArrayList<>();

        @Setting(comment = "This event is fired when the server starts")
        public List<WebHook> server_start = new ArrayList<>();

        @Setting(comment = "This event is fired when the server stops")
        public List<WebHook> server_stop = new ArrayList<>();

        @Setting(comment = "This event is fired when the world is saved")
        public List<WebHook> world_save = new ArrayList<>();

        @Setting(comment = "This event is fired when a world is loaded")
        public List<WebHook> world_load = new ArrayList<>();

        @Setting(comment = "This event is fired when a world is unloaded")
        public List<WebHook> world_unload = new ArrayList<>();


        public Map<WebHookType, List<WebHook>> asMap() {
            Map<WebHookType, List<WebHook>> hooks = new HashMap<>();
            hooks.put(WebHookType.ALL, this.all);
            hooks.put(WebHookType.ADVANCEMENT, this.achievement);
            hooks.put(WebHookType.BLOCK_OPERATION_STATUS, this.block_operation_status);
            hooks.put(WebHookType.CHAT, this.chat);
            hooks.put(WebHookType.COMMAND, this.command);
            hooks.put(WebHookType.INTERACTIVE_MESSAGE, this.interactive_message);
            hooks.put(WebHookType.GENERATE_CHUNK, this.generate_chunk);
            hooks.put(WebHookType.EXPLOSION, this.explosion);
            hooks.put(WebHookType.ENTITY_SPAWN, this.entity_spawn);
            hooks.put(WebHookType.ENTITY_DESPAWN, this.entity_despawn);
            hooks.put(WebHookType.ENTITY_EXPIRE, this.entity_expire);
            hooks.put(WebHookType.INTERACT_BLOCK, this.interact_block);
            hooks.put(WebHookType.INVENTORY_OPEN, this.inventory_open);
            hooks.put(WebHookType.INVENTORY_CLOSE, this.inventory_close);
            hooks.put(WebHookType.PLAYER_JOIN, this.player_join);
            hooks.put(WebHookType.PLAYER_LEAVE, this.player_leave);
            hooks.put(WebHookType.PLAYER_DEATH, this.player_death);
            hooks.put(WebHookType.PLAYER_KICK, this.player_kick);
            hooks.put(WebHookType.PLAYER_BAN, this.player_ban);
            hooks.put(WebHookType.SERVER_START, this.server_start);
            hooks.put(WebHookType.SERVER_STOP, this.server_stop);
            hooks.put(WebHookType.WORLD_SAVE, this.world_save);
            hooks.put(WebHookType.WORLD_LOAD, this.world_load);
            hooks.put(WebHookType.WORLD_UNLOAD, this.world_unload);
            return hooks;
        }
    }
}
