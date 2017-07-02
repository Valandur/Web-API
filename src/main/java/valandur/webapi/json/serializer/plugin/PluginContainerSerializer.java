package valandur.webapi.json.serializer.plugin;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.WebAPI;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class PluginContainerSerializer extends WebAPIBaseSerializer<PluginContainer> {
    @Override
    public void serialize(PluginContainer value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        writeValue(provider, WebAPI.getCacheService().getPlugin(value));
    }
}
