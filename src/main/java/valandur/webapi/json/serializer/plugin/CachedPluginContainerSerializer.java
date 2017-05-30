package valandur.webapi.json.serializer.plugin;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.cache.plugin.CachedPluginContainer;
import valandur.webapi.json.serializer.WebAPISerializer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class CachedPluginContainerSerializer extends WebAPISerializer<CachedPluginContainer> {
    @Override
    public void serialize(CachedPluginContainer value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        writeField(provider, "id", value.getId());
        writeField(provider, "name", value.getName());
        writeField(provider, "version", value.getVersion());

        if (((AtomicBoolean)provider.getAttribute("details")).get()) {
            writeField(provider, "class", value.getClass());
            writeField(provider, "description", value.getDescription());
            writeField(provider, "url", value.getUrl());
            writeField(provider, "authors", value.getAuthors());
        }

        gen.writeEndObject();
    }
}
