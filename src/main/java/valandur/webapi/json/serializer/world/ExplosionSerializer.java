package valandur.webapi.json.serializer.world;

import org.spongepowered.api.world.explosion.Explosion;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class ExplosionSerializer extends WebAPIBaseSerializer<Explosion> {
    @Override
    public void serialize(Explosion value) throws IOException {
        writeStartObject();
        writeField("canCauseFire", value.canCauseFire());
        writeField("radius", value.getRadius());
        writeField("shouldBreakBlocks", value.shouldBreakBlocks());
        writeField("shouldDamageEntities", value.shouldDamageEntities());
        writeField("shouldPlaySmoke", value.shouldPlaySmoke());
        writeField("sourceExplosive", value.getSourceExplosive());
        writeEndObject();
    }
}
