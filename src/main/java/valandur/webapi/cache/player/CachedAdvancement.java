package valandur.webapi.cache.player;

import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.advancement.DisplayInfo;
import org.spongepowered.api.text.Text;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.cache.misc.CachedCatalogType;

public class CachedAdvancement extends CachedCatalogType<Advancement> {

    public String id;
    public String name;
    public Text title;

    @JsonDetails
    public Text description;

    @JsonDetails
    public boolean announceToChat;

    @JsonDetails
    public boolean showToast;

    @JsonDetails
    public boolean hidden;

    @JsonDetails(simple = true)
    public CachedAdvancement parent;

    @JsonDetails
    public AdvancementTree tree;


    public CachedAdvancement(Advancement value) {
        super(value);

        this.id = value.getId();
        this.name = value.getName();
        this.parent = value.getParent().map(CachedAdvancement::new).orElse(null);
        this.tree = value.getTree().orElse(null);

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
