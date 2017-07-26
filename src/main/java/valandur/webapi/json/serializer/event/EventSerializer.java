package valandur.webapi.json.serializer.event;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.block.TargetBlockEvent;
import org.spongepowered.api.event.entity.TargetEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.HandInteractEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.KickPlayerEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.user.BanUserEvent;
import org.spongepowered.api.event.user.TargetUserEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.event.world.GenerateChunkEvent;
import org.spongepowered.api.event.world.TargetWorldEvent;
import valandur.webapi.api.block.IBlockOperationEvent;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class EventSerializer extends WebAPIBaseSerializer<Event> {
    @Override
    public void serialize(Event value) throws IOException {
        writeStartObject();

        writeField("class", value.getClass().getName());

        if (value instanceof TargetEntityEvent) {
            Entity entity = ((TargetEntityEvent)value).getTargetEntity();
            writeField("target", entity);
        } else if (value instanceof TargetUserEvent) {
            writeField("target", ((TargetUserEvent)value).getTargetUser());
        }

        if (value instanceof KickPlayerEvent) {
            String msg = ((KickPlayerEvent)value).getMessage().toPlain();
            writeField("message", msg);
        }

        if (value instanceof BanUserEvent) {
            writeField("ban", ((BanUserEvent)value).getBan());
        }

        /*if (value instanceof GrantAchievementEvent) {
            writeField("achievement", ((GrantAchievementEvent)value).getAchievement());
        }*/

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
        } catch (AbstractMethodError ignored) {}

        writeEndObject();
    }
}
