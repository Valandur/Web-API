package valandur.webapi.cache.player;

import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.advancement.DisplayInfo;
import org.spongepowered.api.text.Text;
import valandur.webapi.api.cache.misc.CachedCatalogType;
import valandur.webapi.api.cache.player.ICachedAdvancement;
import valandur.webapi.api.serialize.JsonDetails;

public class CachedAdvancement extends CachedCatalogType<Advancement> implements ICachedAdvancement {

    private String id;
    @Override
    public String getId() {
        return id;
    }

    private String name;
    @Override
    public String getName() {
        return name;
    }

    private Text title;
    @Override
    public Text getTitle() {
        return title;
    }

    private Text description;
    @Override
    @JsonDetails
    public Text getDescription() {
        return description;
    }

    private boolean announceToChat;
    @Override
    @JsonDetails
    public boolean isAnnounceToChat() {
        return announceToChat;
    }

    private boolean showToast;
    @Override
    @JsonDetails
    public boolean isShowToast() {
        return showToast;
    }

    private boolean hidden;
    @Override
    @JsonDetails
    public boolean isHidden() {
        return hidden;
    }

    private CachedAdvancement parent;
    @Override
    @JsonDetails(simple = true)
    public CachedAdvancement getParent() {
        return parent;
    }

    private CachedCatalogType<AdvancementTree> tree;
    @Override
    @JsonDetails
    public CachedCatalogType<AdvancementTree> getTree() {
        return tree;
    }


    public CachedAdvancement(Advancement value) {
        super(value);

        this.id = value.getId();
        this.name = value.getName();
        this.parent = value.getParent().map(CachedAdvancement::new).orElse(null);
        this.tree = value.getTree().map(CachedCatalogType::new).orElse(null);

        if (value.getDisplayInfo().isPresent()) {
            DisplayInfo info = value.getDisplayInfo().get();
            this.title = info.getTitle();
            this.description = info.getDescription();
            this.announceToChat = info.doesAnnounceToChat();
            this.showToast = info.doesShowToast();
            this.hidden = info.isHidden();
        }
    }
}
