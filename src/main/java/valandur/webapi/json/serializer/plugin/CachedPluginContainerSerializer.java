package valandur.webapi.json.serializer.plugin;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.cache.plugin.CachedPluginContainer;
import valandur.webapi.api.json.WebAPIBaseSerializer;

import java.io.IOException;

public class CachedPluginContainerSerializer extends WebAPIBaseSerializer<CachedPluginContainer> {
    @Override
    public void serialize(CachedPluginContainer value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        writeField(provider, "id", value.getId());
        writeField(provider, "name", value.getName());
        writeField(provider, "version", value.getVersion());

        if (shouldWriteDetails(provider)) {
            writeField(provider, "class", value.getClass());
            writeField(provider, "description", value.getDescription());
            writeField(provider, "url", value.getUrl());
            writeField(provider, "authors", value.getAuthors());
        }

        gen.writeEndObject();
    }
}
