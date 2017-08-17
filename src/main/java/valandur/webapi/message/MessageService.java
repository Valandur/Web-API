package valandur.webapi.message;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.message.IMessage;
import valandur.webapi.api.message.IMessageService;
import valandur.webapi.hook.WebHookService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MessageService implements IMessageService {

    private Map<UUID, Collection<String>> replied = new ConcurrentHashMap<>();

    public boolean sendMessage(IMessage msg) {
        Text.Builder builder = Text.builder().append(msg.getMessage());
        UUID uuid = UUID.randomUUID();

        if (msg.isOnce())
            replied.put(uuid, new ConcurrentHashSet<>());

        if (msg.getOptions().size() > 0) {
            builder.append(Text.of("\n"));

            for (Map.Entry<String, Text> entry : msg.getOptions().entrySet()) {
                final String data = entry.getKey();

                Text opt = entry.getValue().toBuilder().onClick(TextActions.executeCallback(source -> {
                    if (msg.isOnce()) {
                        Collection<String> replies = replied.get(uuid);
                        if (replies.contains(source.getIdentifier())) {
                            source.sendMessage(Text
                                    .builder("You have already replied to this messsage")
                                    .color(TextColors.RED)
                                    .build()
                            );
                            return;
                        }
                        replies.add(source.getIdentifier());
                    }

                    MessageResponse response = new MessageResponse(msg.getId(), data, msg.getTarget());
                    WebAPI.getWebHookService().notifyHooks(WebHookService.WebHookType.CUSTOM_MESSAGE, response);
                })).build();

                builder.append(opt).append(Text.of(" "));
            }
        }

        Text text = builder.build();

        Optional<ICachedPlayer> player = WebAPI.getCacheService().getPlayer(msg.getTarget());
        return player.flatMap(cachedPlayer -> WebAPI.runOnMain(() -> {
            Optional<?> p = cachedPlayer.getLive();
            if (!p.isPresent())
                return false;

            ((Player) p.get()).sendMessage(text);
            return true;
        })).orElse(false);
    }
}
