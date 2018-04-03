/*package valandur.webapi.api.cache.player;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.text.Text;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.misc.ICachedCatalogType;

@ApiModel("Advancement")
public interface ICachedAdvancement extends ICachedObject<Advancement> {

    @ApiModelProperty(value = "The unique id of the advancement", required = true)
    String getId();

    @ApiModelProperty(value = "The name of the advancement", required = true)
    String getName();

    @ApiModelProperty(value = "The title of the advancement", required = true)
    Text getTitle();

    @ApiModelProperty("The description of the advancement")
    Text getDescription();

    @ApiModelProperty("True if the achieving of this advancement is announced in chat, false otherwise")
    boolean isAnnounceToChat();

    @ApiModelProperty("True if achieving this advancement shows the player a toast message, false otherwise")
    boolean isShowToast();

    @ApiModelProperty("True if this is a hidden advancement")
    boolean isHidden();

    @ApiModelProperty("The parent advancement, which must be unlocked prior to this advancement")
    ICachedAdvancement getParent();

    @ApiModelProperty("The advancement tree that this advancement belongs to")
    ICachedCatalogType<AdvancementTree> getTree();
}
*/