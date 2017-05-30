package valandur.webapi.hook.filter;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import valandur.webapi.hook.WebHook;
import valandur.webapi.hook.WebHookFilter;

import java.util.List;

public class PlayerFilter extends WebHookFilter {

    public static String name = "WebAPI-Player";

    private List<String> players;


    public PlayerFilter(WebHook hook, ConfigurationNode config) {
        super(hook, config);

        try {
            players = config.getList(TypeToken.of(String.class));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean process(Object data) {
        if (data instanceof TargetPlayerEvent) {
            TargetPlayerEvent event = (TargetPlayerEvent)data;
            return players.contains(event.getTargetEntity().getUniqueId().toString()) ||
                    players.contains(event.getTargetEntity().getName());
        }

        return false;
    }
}
