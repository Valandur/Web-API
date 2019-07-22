package valandur.webapi.integration.gwmcrates;

import org.gwmdevelopments.sponge_plugin.crates.change_mode.DecorativeItemsChangeMode;
import org.gwmdevelopments.sponge_plugin.crates.preview.Preview;
import org.gwmdevelopments.sponge_plugin.crates.preview.previews.FirstGuiPreview;
import org.gwmdevelopments.sponge_plugin.crates.preview.previews.SecondGuiPreview;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.cache.CachedObject;

import java.util.stream.Collectors;

public class CachedPreview extends CachedObject<Preview> {

    private String type;
    public String getType() {
        return type;
    }


    public CachedPreview(Preview value) {
        super(value);

        this.type = value.getType();

        if (value instanceof FirstGuiPreview) {
            data.put("displayName", ((FirstGuiPreview) value).getDisplayName().orElse(null));
            data.put("scrollDelay", ((FirstGuiPreview) value).getScrollDelay());
            data.put("decorativeItems", ((FirstGuiPreview) value).getDecorativeItems().stream()
                    .map(ItemStack::copy).collect(Collectors.toList()));
            data.put("decorativeItemsChangeDelay",
                    ((FirstGuiPreview) value).getDecorativeItemsChangeMode()
                            .map(DecorativeItemsChangeMode::getChangeDelay).orElse(null));
            data.put("decorativeItemsIgnoredIndices",
                    ((FirstGuiPreview) value).getDecorativeItemsChangeMode()
                            .map(DecorativeItemsChangeMode::getIgnoredIndices).orElse(null));
        } else if (value instanceof SecondGuiPreview) {
            data.put("displayName", ((SecondGuiPreview) value).getDisplayName().orElse(null));
        }
    }

    @Override
    public String getLink() {
        return null;
    }
}
