package valandur.webapi.serialize.view.player;

import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.advancement.DisplayInfo;
import org.spongepowered.api.text.Text;
import valandur.webapi.api.serialize.BaseView;
import valandur.webapi.api.serialize.JsonDetails;

public class AdvancementView extends BaseView<Advancement> {

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
    public Advancement parent;

    @JsonDetails
    public AdvancementTree tree;


    public AdvancementView(Advancement value) {
        super(value);

        this.id = value.getId();
        this.name = value.getName();
        this.parent = value.getParent().orElse(null);
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
