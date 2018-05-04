package valandur.webapi.hook.filter;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import valandur.webapi.api.hook.BaseWebHookFilter;
import valandur.webapi.hook.WebHook;

import java.util.List;

public class PlayerFilter extends BaseWebHookFilter {

    public static String name = "WebAPI-Player";
    @Override
    public String getName() {
        return name;
    }

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
    protected void _writeToConfig(ConfigurationNode node) throws ObjectMappingException {
        node.getNode("config").setValue(new TypeToken<List<String>>() {}, this.players);
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
