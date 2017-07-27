package valandur.webapi.api.message;

/**
 * The message service allows sending interactive messages to online players.
 * These messages will show up in the chat and feature clickable answers, which will trigger a web hook-like response.
 */
public interface IMessageService {

    /**
     * Sends a new interactive message to a player.
     * @param msg The message to send.
     * @return True if it was sent successfully, false otherwise.
     */
    boolean sendMessage(IMessage msg);
}
