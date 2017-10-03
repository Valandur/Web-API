package valandur.webapi.message;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.message.IMessage;
import valandur.webapi.api.message.IMessageOption;
import valandur.webapi.api.message.IMessageService;
import valandur.webapi.hook.WebHookService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class MessageService implements IMessageService {

    private Map<UUID, Set<String>> replied = new ConcurrentHashMap<>();

    public boolean sendMessage(IMessage msg) {
        Text.Builder builder = Text.builder();
        UUID uuid = UUID.randomUUID();

        if (msg.getMessage() != null) {
            builder.append(msg.getMessage());
        }

        if (msg.isOnce() != null && msg.isOnce()) {
            replied.put(uuid, new ConcurrentSkipListSet<>());
        }

        if (msg.hasOptions() && msg.getOptions().size() > 0) {
            builder.append(Text.of("\n"));

            for (IMessageOption option : msg.getOptions()) {
                final String data = option.getKey();

                Text opt = option.getValue().toBuilder().onClick(TextActions.executeCallback(source -> {
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

                    MessageResponse response = new MessageResponse(msg.getId(), data, source.getIdentifier());
                    WebAPI.getWebHookService().notifyHooks(WebHookService.WebHookType.CUSTOM_MESSAGE, response);
                })).build();

                builder.append(opt).append(Text.of(" "));
            }
        }

        Text text = builder.build();

        List<ICachedPlayer> cachedPlayers = new ArrayList<>();
        if (msg.getTarget() != null) {
            Optional<ICachedPlayer> player = WebAPI.getCacheService().getPlayer(msg.getTarget());
            player.map(cachedPlayers::add);
        } else {
            msg.getTargets().forEach(u -> WebAPI.getCacheService().getPlayer(u).map(cachedPlayers::add));
        }

        return WebAPI.runOnMain(() -> {
            List<Player> players = new ArrayList<>();
            for (ICachedPlayer player : cachedPlayers) {
                Optional<?> p = player.getLive();
                if (!p.isPresent())
                    return false;

                players.add((Player)p.get());
            }

            for (Player player : players) {
                player.sendMessage(text);
            }

            return true;
        }).orElse(false);
    }
}
