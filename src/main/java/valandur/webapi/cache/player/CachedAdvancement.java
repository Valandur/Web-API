/*package valandur.webapi.cache.player;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.advancement.DisplayInfo;
import org.spongepowered.api.text.Text;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.serialize.JsonDetails;

@ApiModel("Advancement")
public class CachedAdvancement extends CachedCatalogType<Advancement> {

    private String id;
    @Override
    @ApiModelProperty(value = "The unique id of the advancement", required = true)
    public String getId() {
        return id;
    }

    private String name;
    @Override
    @ApiModelProperty(value = "The name of the advancement", required = true)
    public String getName() {
        return name;
    }

    private Text title;
    @ApiModelProperty(value = "The title of the advancement", required = true)
    public Text getTitle() {
        return title;
    }

    private Text description;
    @JsonDetails
    @ApiModelProperty("The description of the advancement")
    public Text getDescription() {
        return description;
    }

    private boolean announceToChat;
    @JsonDetails
    @ApiModelProperty("True if the achieving of this advancement is announced in chat, false otherwise")
    public boolean isAnnounceToChat() {
        return announceToChat;
    }

    private boolean showToast;
    @JsonDetails
    @ApiModelProperty("True if achieving this advancement shows the player a toast message, false otherwise")
    public boolean isShowToast() {
        return showToast;
    }

    private boolean hidden;
    @JsonDetails
    @ApiModelProperty("True if this is a hidden advancement")
    public boolean isHidden() {
        return hidden;
    }

    private CachedAdvancement parent;
    @JsonDetails(simple = true)
    @ApiModelProperty("The parent advancement, which must be unlocked prior to this advancement")
    public CachedAdvancement getParent() {
        return parent;
    }

    private CachedCatalogType<AdvancementTree> tree;
    @JsonDetails
    @ApiModelProperty("The advancement tree that this advancement belongs to")
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
*/