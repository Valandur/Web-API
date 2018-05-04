package valandur.webapi.serialize.view.event;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.achievement.GrantAchievementEvent;
import org.spongepowered.api.event.block.TargetBlockEvent;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.TargetEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.HandInteractEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.KickPlayerEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.user.BanUserEvent;
import org.spongepowered.api.event.user.TargetUserEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.event.world.GenerateChunkEvent;
import org.spongepowered.api.event.world.TargetWorldEvent;
import valandur.webapi.api.block.IBlockOperationEvent;
import valandur.webapi.api.cache.misc.CachedCatalogType;
import valandur.webapi.api.serialize.BaseView;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.cache.command.CachedCommandResult;

import java.util.HashMap;
import java.util.Map;

@ApiModel("Event")
public class EventView extends BaseView<Event> {

    @JsonProperty(value = "class")
    @ApiModelProperty(value = "The class of event", required = true)
    public String clazz;

    private Map<String, Object> data = new HashMap<>();
    @JsonAnyGetter
    public Map<String, Object> getData() {
        return data;
    }


    public EventView(Event value) {
        super(value);

        this.clazz = value.getClass().getName();

        if (value instanceof TargetEntityEvent) {
            data.put("target", ((TargetEntityEvent)value).getTargetEntity());
        } else if (value instanceof TargetPlayerEvent) {
            // TODO: Not sure if we really need this clause...
            data.put("target", ((TargetPlayerEvent)value).getTargetEntity());
        } else if (value instanceof TargetUserEvent) {
            data.put("target", ((TargetUserEvent)value).getTargetUser());
        }

        if (value instanceof MessageChannelEvent) {
            MessageChannelEvent event = (MessageChannelEvent)value;
            data.put("message", event.getMessage());
            data.put("receivers", event.getChannel().orElse(event.getOriginalChannel()).getMembers());
        }

        if (value instanceof SendCommandEvent) {
            SendCommandEvent event = (SendCommandEvent)value;
            data.put("command", event.getCommand());
            data.put("arguments", event.getArguments());
            data.put("result", new CachedCommandResult(event.getResult()));
        }

        if (value instanceof KickPlayerEvent) {
            data.put("message", ((KickPlayerEvent)value).getMessage());
        }

        if (value instanceof BanUserEvent) {
            data.put("ban", ((BanUserEvent)value).getBan());
        }

        if (value instanceof GrantAchievementEvent) {
            data.put("achievement", ((GrantAchievementEvent)value).getAchievement());
        }

        if (value instanceof InteractInventoryEvent) {
            data.put("inventory", ((InteractInventoryEvent)value).getTargetInventory());
        }

        if (value instanceof IBlockOperationEvent) {
            data.put("operation", ((IBlockOperationEvent)value).getBlockOperation());
        }

        if (value instanceof GenerateChunkEvent) {
            data.put("chunk", ((GenerateChunkEvent)value).getTargetChunk());
        }

        if (value instanceof ExplosionEvent) {
            data.put("explosion", ((ExplosionEvent)value).getExplosion());
        }

        if (value instanceof TargetWorldEvent) {
            data.put("world", ((TargetWorldEvent)value).getTargetWorld());
        }

        if (value instanceof TargetBlockEvent) {
            data.put("block", ((TargetBlockEvent)value).getTargetBlock());
        }

        if (value instanceof HandInteractEvent) {
            data.put("hand", ((HandInteractEvent)value).getHandType());
        }

        try {
            data.put("cause", value.getCause());
        } catch (AbstractMethodError ignored) {}
    }
}
