package valandur.webapi.hook.filter;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.event.block.TargetBlockEvent;
import valandur.webapi.hook.WebHookFilter;
import valandur.webapi.hook.WebHook;

import java.util.List;

public class BlockTypeFilter extends WebHookFilter {

    public static String name = "WebAPI-BlockType";

    private List<BlockType> types;


    public BlockTypeFilter(WebHook hook, ConfigurationNode config) {
        super(hook, config);

        try {
            types = config.getList(TypeToken.of(BlockType.class));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean process(Object data) {
        if (data instanceof TargetBlockEvent) {
            TargetBlockEvent event = (TargetBlockEvent)data;
            return types.contains(event.getTargetBlock().getState().getType());
        }

        return false;
    }
}
