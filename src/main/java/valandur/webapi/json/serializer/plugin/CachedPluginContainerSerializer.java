package valandur.webapi.json.serializer.plugin;

import valandur.webapi.api.cache.plugin.ICachedPluginDependency;
import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.cache.plugin.CachedPluginContainer;

import java.io.IOException;

public class CachedPluginContainerSerializer extends WebAPIBaseSerializer<CachedPluginContainer> {

    @Override
    public void serialize(CachedPluginContainer value) throws IOException {
        writeStartObject();

        writeField("id", value.getId());
        writeField("name", value.getName());
        writeField("version", value.getVersion());

        if (shouldWriteDetails()) {
            writeField("description", value.getDescription());
            writeField("url", value.getUrl());
            writeField("authors", value.getAuthors());
            writeArrayFieldStart("dependencies");
            for (ICachedPluginDependency dependency : value.getDependencies()) {
                writeValue(dependency);
            }
            writeEndArray();
        }

        writeEndObject();
    }
}
