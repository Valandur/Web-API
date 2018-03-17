package valandur.webapi.message;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.cache.player.ICachedPlayerFull;
import valandur.webapi.api.message.IInteractiveMessage;
import valandur.webapi.api.message.IInteractiveMessageOption;
import valandur.webapi.api.message.IInteractiveMessageService;
import valandur.webapi.hook.WebHookService;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class InteractiveMessageService implements IInteractiveMessageService {

    private Map<UUID, IInteractiveMessage> messages = new ConcurrentHashMap<>();
    private Map<UUID, Set<String>> replied = new ConcurrentHashMap<>();

    @Override
    public List<IInteractiveMessage> getMessages() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public void sendMessage(IInteractiveMessage msg) {
        Text.Builder builder = Text.builder();

        if (msg.getMessage() != null) {
            builder.append(msg.getMessage());
        }

        if (msg.isOnce() != null && msg.isOnce()) {
            replied.put(msg.getUUID(), new ConcurrentSkipListSet<>());
        }

        if (msg.hasOptions() && msg.getOptions().size() > 0) {
            builder.append(Text.of("\n"));

            for (IInteractiveMessageOption option : msg.getOptions()) {
                final String data = option.getKey();

                Text opt = option.getValue().toBuilder().onClick(TextActions.executeCallback(source -> {
                    if (msg.isOnce()) {
                        Collection<String> replies = replied.get(msg.getUUID());
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

                    InteractiveMessageResponse response = new InteractiveMessageResponse(msg.getId(), data, source.getIdentifier());
                    WebAPI.getWebHookService().notifyHooks(WebHookService.WebHookType.INTERACTIVE_MESSAGE, response);
                })).build();

                builder.append(opt).append(Text.of(" "));
            }
        }

        Text text = builder.build();

        List<ICachedPlayer> cachedPlayers = new ArrayList<>();
        if (msg.getTarget() != null) {
            if (msg.getTarget().equalsIgnoreCase("server")) {
                cachedPlayers.addAll(WebAPI.getCacheService().getPlayers());
            } else {
                Optional<ICachedPlayerFull> player = WebAPI.getCacheService().getPlayer(msg.getTarget());
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
            for (ICachedPlayer player : cachedPlayers) {
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

    @Override
    public Optional<IInteractiveMessage> getMessage(UUID uuid) {
        IInteractiveMessage msg = messages.get(uuid);
        return msg != null ? Optional.of(msg) : Optional.empty();
    }
}
