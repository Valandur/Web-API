package valandur.webapi.json.serializer.plugin;

import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.servlet.plugin.CachedPluginDependency;

import java.io.IOException;

public class CachedPluginDependencySerializer extends WebAPIBaseSerializer<CachedPluginDependency> {

    @Override
    protected void serialize(CachedPluginDependency value) throws IOException {
        writeStartObject();
        writeField("id", value.getId());
        writeField("version", value.getVersion());
        writeField("loadOrder", value.getLoadOrder().toString());
        writeField("optional", value.isOptional());
        writeEndObject();
    }
}
