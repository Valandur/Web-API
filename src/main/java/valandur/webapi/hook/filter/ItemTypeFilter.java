package valandur.webapi.hook.filter;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.event.entity.item.TargetItemEvent;
import org.spongepowered.api.item.ItemType;
import valandur.webapi.hook.WebHook;
import valandur.webapi.hook.WebHookFilter;

import java.util.List;

public class ItemTypeFilter extends WebHookFilter {

    public static String name = "WebAPI-Item";

    private List<ItemType> items;


    public ItemTypeFilter(WebHook hook, ConfigurationNode config) {
        super(hook, config);

        try {
            items = config.getList(TypeToken.of(ItemType.class));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean process(Object data) {
        if (data instanceof TargetItemEvent) {
            TargetItemEvent event = (TargetItemEvent)data;
            return items.contains(event.getTargetEntity().getItemType());
        }

        return false;
    }
}
