package valandur.webapi.json.serializer.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.world.explosion.Explosion;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class ExplosionSerializer extends WebAPISerializer<Explosion> {
    @Override
    public void serialize(Explosion value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "canCauseFire", value.canCauseFire());
        writeField(provider, "radius", value.getRadius());
        writeField(provider, "shouldBreakBlocks", value.shouldBreakBlocks());
        writeField(provider, "shouldDamageEntities", value.shouldDamageEntities());
        writeField(provider, "shouldPlaySmoke", value.shouldPlaySmoke());
        writeField(provider, "sourceExplosive", value.getSourceExplosive());
        gen.writeEndObject();
    }
}
