package valandur.webapi.api.cache.chat;

import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.player.ICachedPlayer;

public interface ICachedChatMessage extends ICachedObject {

    Long getTimestamp();

    ICachedPlayer getSender();

    String getMessage();
}
