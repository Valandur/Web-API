package valandur.webapi.message;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.DataCache;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.hook.WebHooks;

import java.util.Map;
import java.util.Optional;

public class Messages {
    public static boolean sendMessage(Message msg) {
        Text.Builder builder = Text.builder().append(msg.getMessage());

        if (msg.getOptions().size() > 0) {
            builder.append(Text.of("\n"));

            for (Map.Entry<String, String> entry : msg.getOptions().entrySet()) {
                final String data = entry.getKey();

                Text opt = Text.builder("[" + entry.getValue() + "]").onClick(TextActions.executeCallback(source -> {
                    MessageResponse response = new MessageResponse(msg.getId(), data, msg.getTarget());
                    WebHooks.notifyHooks(WebHooks.WebHookType.CUSTOM_MESSAGE, response);
                })).color(TextColors.BLUE).style(TextStyles.UNDERLINE).build();

                builder.append(opt).append(Text.of(" "));
            }
        }

        Text text = builder.build();

        Optional<CachedPlayer> player = DataCache.getPlayer(msg.getTarget());
        return player.flatMap(cachedPlayer -> WebAPI.runOnMain(() -> {
            Optional<?> p = cachedPlayer.getLive();
            if (!p.isPresent())
                return false;

            ((Player) p.get()).sendMessage(text);
            return true;
        })).orElse(false);
    }
}
