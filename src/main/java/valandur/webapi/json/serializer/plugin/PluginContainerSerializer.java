package valandur.webapi.json.serializer.plugin;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;

public class PluginContainerSerializer extends WebAPISerializer<PluginContainer> {
    @Override
    public void serialize(PluginContainer value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, DataCache.getPlugin(value));
    }
}
