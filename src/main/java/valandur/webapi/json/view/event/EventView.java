package valandur.webapi.json.view.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Event;
import valandur.webapi.api.json.BaseView;

public class EventView extends BaseView<Event> {

    @JsonProperty(value = "class")
    public String clazz;

    public Entity target;

    public EventView(Event value) {
        super(value);

        this.clazz = value.getClass().getName();

        /*if (value instanceof TargetEntityEvent) {
            target = ((TargetEntityEvent)value).getTargetEntity();
        } else if (value instanceof TargetUserEvent) {
            target = ((TargetUserEvent)value).getTargetUser();
        }

        if (value instanceof KickPlayerEvent) {
            String msg = ((KickPlayerEvent)value).getMessage().toPlain();
            writeField("message", msg);
        }

        if (value instanceof BanUserEvent) {
            writeField("ban", ((BanUserEvent)value).getBan());
        }

        if (value instanceof GrantAchievementEvent) {
            writeField("achievement", ((GrantAchievementEvent)value).getAchievement());
        }

        if (value instanceof InteractInventoryEvent) {
            writeField("inventory", ((InteractInventoryEvent)value).getTargetInventory());
        }

        if (value instanceof IBlockOperationEvent) {
            writeField("blockUpdate", ((IBlockOperationEvent)value).getBlockOperation());
        }

        if (value instanceof GenerateChunkEvent) {
            writeField("chunk", ((GenerateChunkEvent)value).getTargetChunk());
        }

        if (value instanceof ExplosionEvent) {
            writeField("explosion", ((ExplosionEvent)value).getExplosion());
        }

        if (value instanceof TargetWorldEvent) {
            writeField("world", ((TargetWorldEvent)value).getTargetWorld());
        }

        if (value instanceof TargetBlockEvent) {
            writeField("block", ((TargetBlockEvent)value).getTargetBlock());
        }

        if (value instanceof HandInteractEvent) {
            writeField("hand", ((HandInteractEvent)value).getHandType());
        }

        try {
            writeField("cause", value.getCause());
        } catch (AbstractMethodError ignored) {}*/
    }
}
