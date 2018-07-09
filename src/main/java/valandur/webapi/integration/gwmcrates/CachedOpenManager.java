package valandur.webapi.integration.gwmcrates;

import org.gwmdevelopments.sponge_plugin.crates.change_mode.DecorativeItemsChangeMode;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.OpenManager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.open_managers.Animation1OpenManager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.open_managers.FirstOpenManager;
import org.gwmdevelopments.sponge_plugin.crates.open_manager.open_managers.SecondOpenManager;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;

import java.util.stream.Collectors;

public class CachedOpenManager extends CachedObject<OpenManager> {

    private String type;
    public String getType() {
        return type;
    }

    private CachedCatalogType<SoundType> openSound;
    public CachedCatalogType<SoundType> getOpenSound() {
        return openSound;
    }


    public CachedOpenManager(OpenManager value) {
        super(value);

        this.type = value.getType();
        this.openSound = value.getOpenSound().map(CachedCatalogType::new).orElse(null);

        if (value instanceof FirstOpenManager) {
            data.put("displayName", ((FirstOpenManager) value).getDisplayName().orElse(null));
            data.put("closeDelay", ((FirstOpenManager) value).getCloseDelay());
            data.put("decorativeItems", ((FirstOpenManager) value).getDecorativeItems().stream()
                    .map(ItemStack::copy).collect(Collectors.toList()));
            data.put("decorativeItemsChangeDelay",
                    ((FirstOpenManager) value).getDecorativeItemsChangeMode()
                            .map(DecorativeItemsChangeMode::getChangeDelay).orElse(null));
            data.put("decorativeItemsIgnoredIndices",
                    ((FirstOpenManager) value).getDecorativeItemsChangeMode()
                            .map(DecorativeItemsChangeMode::getIgnoredIndices).orElse(null));
            data.put("scrollDelays", ((FirstOpenManager) value).getScrollDelays());
            data.put("scrollSound",
                    ((FirstOpenManager) value).getScrollSound().map(CachedCatalogType::new).orElse(null));
            data.put("winSound",
                    ((FirstOpenManager) value).getWinSound().map(CachedCatalogType::new).orElse(null));
        } else if (value instanceof SecondOpenManager) {
            data.put("displayName", ((SecondOpenManager) value).getDisplayName().orElse(null));
            data.put("closeDelay", ((SecondOpenManager) value).getCloseDelay());
            data.put("clickSound",
                    ((SecondOpenManager) value).getClickSound().map(CachedCatalogType::new).orElse(null));
            data.put("hiddenItem", ((SecondOpenManager) value).getHiddenItem().copy());
            data.put("rows", ((SecondOpenManager) value).getRows());
            data.put("showOtherDropsDelay", ((SecondOpenManager) value).getShowOtherDropsDelay());
        } else if (value instanceof Animation1OpenManager) {
            data.put("closeDelay", ((Animation1OpenManager) value).getCloseDelay());
            data.put("crate", new CachedCatalogType<>(((Animation1OpenManager) value).getCrateBlockType()));
            data.put("fence", new CachedCatalogType<>(((Animation1OpenManager) value).getFenceBlockType()));
            data.put("floor", new CachedCatalogType<>(((Animation1OpenManager) value).getFloorBlockType()));
        }
    }
}
