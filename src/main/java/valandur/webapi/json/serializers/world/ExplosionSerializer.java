package valandur.webapi.json.serializers.world;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.world.explosion.Explosion;
import valandur.webapi.json.serializers.WebAPISerializer;

import java.io.IOException;

public class ExplosionSerializer extends WebAPISerializer<Explosion> {
    @Override
    public void serialize(Explosion value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeBooleanField("canCauseFire", value.canCauseFire());
        gen.writeNumberField("radius", value.getRadius());
        gen.writeBooleanField("shouldBreakBlocks", value.shouldBreakBlocks());
        gen.writeBooleanField("shouldDamageEntities", value.shouldDamageEntities());
        gen.writeBooleanField("shouldPlaySmoke", value.shouldPlaySmoke());
        gen.writeObjectField("sourceExplosive", value.getSourceExplosive());
        gen.writeEndObject();
    }
}
