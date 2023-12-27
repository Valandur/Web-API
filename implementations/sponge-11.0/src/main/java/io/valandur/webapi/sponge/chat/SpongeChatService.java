package io.valandur.webapi.sponge.chat;

import io.valandur.webapi.chat.ChatMessage;
import io.valandur.webapi.chat.ChatService;
import io.valandur.webapi.chat.ChatSource;
import io.valandur.webapi.sponge.SpongeWebAPI;
import java.time.Instant;
import net.kyori.adventure.chat.ChatType.Bound;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.ChatTypes;
import org.spongepowered.api.command.manager.CommandMapping;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.message.PlayerChatEvent;
import org.spongepowered.api.event.message.SystemMessageEvent;
import org.spongepowered.api.util.Identifiable;

public class SpongeChatService extends ChatService<SpongeWebAPI> {

  private static final Bound webApiChatType = ChatTypes.CHAT.get()
      .bind(MiniMessage.miniMessage().deserialize("<blue>[Web-API]</blue>"));

  public SpongeChatService(SpongeWebAPI webapi) {
    super(webapi);
  }

  @Override
  public void sendChatMessage(String message) {
    var msg = SignedMessage.system(message, null);
    Sponge.server().broadcastAudience().sendMessage(msg, webApiChatType);

    var source = new ChatSource(null, null, true);
    this.chatMessages.add(new ChatMessage(Instant.now(), message, source));
  }

  @Listener
  public void onPlayerMessage(PlayerChatEvent.Submit event) {
    var msg = PlainTextComponentSerializer.plainText().serialize(event.message());

    var cause = event.cause();
    var playerId = cause.first(Player.class).map(Identifiable::uniqueId).orElse(null);
    var cmd = cause.first(CommandMapping.class).map(CommandMapping::primaryAlias).orElse(null);
    var isFromServer = cause.first(Server.class).isPresent();

    var source = new ChatSource(playerId, cmd, isFromServer);
    this.chatMessages.add(new ChatMessage(Instant.now(), msg, source));
  }

  @Listener
  public void onSystemMessage(SystemMessageEvent event) {
    var msg = PlainTextComponentSerializer.plainText().serialize(event.message());

    var cause = event.cause();
    var playerId = cause.first(Player.class).map(Identifiable::uniqueId).orElse(null);
    var cmd = cause.first(CommandMapping.class).map(CommandMapping::primaryAlias).orElse(null);
    var isFromServer = cause.first(Server.class).isPresent();

    var source = new ChatSource(playerId, cmd, isFromServer);
    this.chatMessages.add(new ChatMessage(Instant.now(), msg, source));
  }
}
