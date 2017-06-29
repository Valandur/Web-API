package valandur.webapi.api.service;

import org.spongepowered.api.event.Event;

public interface IWebHookService {

    /**
     * Some base types of WebHooks that are included with the WebAPI
     */
    enum WebHookType {
        ALL, CUSTOM_COMMAND, CUSTOM_EVENT, CUSTOM_MESSAGE,
        ACHIEVEMENT, BLOCK_UPDATE_STATUS, CHAT, COMMAND, GENERATE_CHUNK, EXPLOSION, INTERACT_BLOCK, INVENTORY_OPEN,
        INVENTORY_CLOSE, PLAYER_JOIN, PLAYER_LEAVE, PLAYER_DEATH, PLAYER_KICK, PLAYER_BAN, SERVER_START, SERVER_STOP,
        WORLD_SAVE, WORLD_LOAD, WORLD_UNLOAD
    }

    /**
     * Trigger a WebHook of the specified type, sending along the specified data.
     * @param type The type of WebHook
     * @param data The data that is sent to the endpoints.
     */
    void notifyHooks(WebHookType type, Object data);

    /**
     * Trigger an event WebHook for the specified event.
     * @param clazz The class of event for which the WebHooks are triggered.
     * @param data The data that is sent to the endpoints.
     */
    void notifyHooks(Class<? extends Event> clazz, Object data);
}
