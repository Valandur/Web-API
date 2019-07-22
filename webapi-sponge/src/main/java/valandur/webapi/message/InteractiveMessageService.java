package valandur.webapi.message;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.hook.WebHookService;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * The message service allows sending interactive messages to online players.
 * These messages will show up in the chat and feature clickable answers, which will trigger a web hook-like response.
 */
public class InteractiveMessageService {

    private Map<UUID, InteractiveMessage> messages = new ConcurrentHashMap<>();
    private Map<UUID, Set<String>> replied = new ConcurrentHashMap<>();

    /**
     * Gets all the messages that were sent since server start.
     * @return The messages sent since server start.
     */
    public List<InteractiveMessage> getMessages() {
        return new ArrayList<>(messages.values());
    }

    /**
     * Sends a new interactive message to a player.
     * @param msg The message to send.
     */
    public void sendMessage(InteractiveMessage msg) {
        Text.Builder builder = Text.builder();

        if (msg.getMessage() != null) {
            builder.append(msg.getMessage());
        }

        if (msg.isOnce()) {
            replied.put(msg.getUUID(), new ConcurrentSkipListSet<>());
        }

        if (msg.hasOptions() && msg.getOptions().size() > 0) {
            builder.append(Text.of("\n"));

            for (InteractiveMessageOption option : msg.getOptions()) {
                final String data = option.getKey();

                Text opt = option.getValue().toBuilder().onClick(TextActions.executeCallback(source -> {
                    if (msg.isOnce()) {
                        Collection<String> replies = replied.get(msg.getUUID());
                        if (replies.contains(source.getIdentifier())) {
                            source.sendMessage(Text
                                    .builder("You have already replied to this message")
                                    .color(TextColors.RED)
                                    .build()
                            );
                            return;
                        }
                        replies.add(source.getIdentifier());
                    }

                    InteractiveMessageResponse response = new InteractiveMessageResponse(
                            msg.getId(), data, source.getIdentifier());
                    WebAPI.getWebHookService().notifyHooks(WebHookService.WebHookType.INTERACTIVE_MESSAGE, response);
                })).build();

                builder.append(opt).append(Text.of(" "));
            }
        }

        Text text = builder.build();

        List<CachedPlayer> cachedPlayers = new ArrayList<>();
        if (msg.getTarget() != null) {
            if (msg.getTarget().equalsIgnoreCase("server")) {
                cachedPlayers.addAll(WebAPI.getCacheService().getPlayers());
            } else {
                Optional<CachedPlayer> player = WebAPI.getCacheService().getPlayer(msg.getTarget());
                player.map(cachedPlayers::add);
            }
        } else {
            msg.getTargets().forEach(u -> WebAPI.getCacheService().getPlayer(u).map(cachedPlayers::add));
        }

        if (cachedPlayers.size() == 0) {
            throw new BadRequestException("No valid targets defined");
        }

        messages.put(msg.getUUID(), msg);

        WebAPI.runOnMain(() -> {
            List<Player> players = new ArrayList<>();
            for (CachedPlayer player : cachedPlayers) {
                Optional<?> p = player.getLive();
                if (!p.isPresent())
                    throw new InternalServerErrorException("Could not get live player");

                players.add((Player)p.get());
            }

            for (Player player : players) {
                player.sendMessage(text);
            }
        });
    }

    /**
     * Gets a message by its uuid.
     * @param uuid The uuid of the message.
     * @return An optional containing the message if found, otherwise an empty optional.
     */
    public Optional<InteractiveMessage> getMessage(UUID uuid) {
        InteractiveMessage msg = messages.get(uuid);
        return msg != null ? Optional.of(msg) : Optional.empty();
    }
}
