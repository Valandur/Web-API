package valandur.webapi.json.serializer.plugin;

import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.WebAPI;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class PluginContainerSerializer extends WebAPIBaseSerializer<PluginContainer> {
    @Override
    public void serialize(PluginContainer value) throws IOException {
        writeValue(WebAPI.getCacheService().getPlugin(value));
    }
}
