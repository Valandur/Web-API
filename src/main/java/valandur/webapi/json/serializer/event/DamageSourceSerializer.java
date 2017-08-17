package valandur.webapi.json.serializer.event;

import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class DamageSourceSerializer extends WebAPIBaseSerializer<DamageSource> {

    @Override
    protected void serialize(DamageSource value) throws IOException {
        writeStartObject();
        writeField("affectsCreative", value.doesAffectCreative());
        writeField("isAbsolute", value.isAbsolute());
        writeField("isBypassingArmour", value.isBypassingArmor());
        writeField("isExplosive", value.isExplosive());
        writeField("isMagic", value.isMagic());
        writeField("isScaledByDifficulty", value.isScaledByDifficulty());
        writeField("damageType", value.getType());
        writeEndObject();
    }
}
