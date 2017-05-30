package valandur.webapi.json.serializer.event;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.achievement.GrantAchievementEvent;
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
import valandur.webapi.block.BlockUpdateEvent;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class EventSerializer extends WebAPISerializer<Event> {
    @Override
    public void serialize(Event value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        writeField(provider, "class", value.getClass().getName());

        if (value instanceof TargetEntityEvent) {
            Entity entity = ((TargetEntityEvent)value).getTargetEntity();
            writeField(provider, "target", entity);
        } else if (value instanceof TargetUserEvent) {
            writeField(provider, "target", ((TargetUserEvent)value).getTargetUser());
        }

        if (value instanceof KickPlayerEvent) {
            String msg = ((KickPlayerEvent)value).getMessage().toPlain();
            writeField(provider, "message", msg);
        }

        if (value instanceof BanUserEvent) {
            writeField(provider, "ban", ((BanUserEvent)value).getBan());
        }

        if (value instanceof GrantAchievementEvent) {
            writeField(provider, "achievement", ((GrantAchievementEvent)value).getAchievement());
        }

        if (value instanceof InteractInventoryEvent) {
            writeField(provider, "inventory", ((InteractInventoryEvent)value).getTargetInventory());
        }

        if (value instanceof BlockUpdateEvent) {
            writeField(provider, "blockUpdate", ((BlockUpdateEvent)value).getBlockUpdate());
        }

        if (value instanceof GenerateChunkEvent) {
            writeField(provider, "chunk", ((GenerateChunkEvent)value).getTargetChunk());
        }

        if (value instanceof ExplosionEvent) {
            writeField(provider, "explosion", ((ExplosionEvent)value).getExplosion());
        }

        if (value instanceof TargetWorldEvent) {
            writeField(provider, "world", ((TargetWorldEvent)value).getTargetWorld());
        }

        if (value instanceof TargetBlockEvent) {
            writeField(provider, "block", ((TargetBlockEvent)value).getTargetBlock());
        }

        if (value instanceof HandInteractEvent) {
            writeField(provider, "hand", ((HandInteractEvent)value).getHandType());
        }

        try {
            writeField(provider, "cause", value.getCause());
        } catch (AbstractMethodError ignored) {}

        gen.writeEndObject();
    }
}
