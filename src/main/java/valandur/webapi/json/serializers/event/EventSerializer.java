package valandur.webapi.json.serializers.event;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.achievement.GrantAchievementEvent;
import org.spongepowered.api.event.entity.TargetEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.KickPlayerEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.user.BanUserEvent;
import org.spongepowered.api.event.user.TargetUserEvent;
import valandur.webapi.blocks.BlockUpdateEvent;
import valandur.webapi.cache.CachedEntity;
import valandur.webapi.cache.CachedPlayer;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class EventSerializer extends WebAPISerializer<Event> {
    @Override
    public void serialize(Event value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        gen.writeStringField("class", value.getClass().getName());

        if (value instanceof TargetEntityEvent) {
            Entity entity = ((TargetEntityEvent)value).getTargetEntity();
            if (entity instanceof Player) {
                CachedPlayer p = DataCache.getPlayer((Player)entity);
                gen.writeObjectField("target", p);
            } else {
                CachedEntity e = DataCache.getEntity(entity);
                gen.writeObjectField("target", e);
            }
        } else if (value instanceof TargetUserEvent) {
            gen.writeObjectField("target", ((TargetUserEvent)value).getTargetUser());
        }

        if (value instanceof KickPlayerEvent) {
            String msg = ((KickPlayerEvent)value).getMessage().toPlain();
            gen.writeStringField("message", msg);
        }

        if (value instanceof BanUserEvent) {
            gen.writeObjectField("ban", ((BanUserEvent)value).getBan());
        }

        if (value instanceof GrantAchievementEvent) {
            gen.writeObjectField("achievement", ((GrantAchievementEvent)value).getAchievement());
        }

        if (value instanceof InteractInventoryEvent) {
            gen.writeObjectField("inventory", ((InteractInventoryEvent)value).getTargetInventory());
        }

        if (value instanceof BlockUpdateEvent) {
            gen.writeObjectField("blockUpdate", ((BlockUpdateEvent)value).getBlockUpdate());
        }

        try {
            gen.writeObjectField("cause", value.getCause());
        } catch (AbstractMethodError ignored) {}

        gen.writeEndObject();
    }
}
