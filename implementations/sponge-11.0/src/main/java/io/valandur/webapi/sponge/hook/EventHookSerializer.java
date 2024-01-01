package io.valandur.webapi.sponge.hook;

import io.valandur.webapi.hook.EventHook;
import io.valandur.webapi.hook.HookDataType;
import io.valandur.webapi.hook.HookEventType;
import io.valandur.webapi.hook.HookHeader;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class EventHookSerializer implements TypeSerializer<EventHook> {

    @Override
    public EventHook deserialize(Type type, ConfigurationNode node)
            throws SerializationException {
        var eventType = node.node("event").get(HookEventType.class);
        var enabled = node.node("enabled").getBoolean();
        var method = node.node("method").getString();
        var address = node.node("address").getString();
        var dataType = node.node("type").get(HookDataType.class);
        var headers = new ArrayList<HookHeader>();
        for (var entry : node.node("headers").childrenMap().entrySet()) {
            headers.add(new HookHeader(entry.getKey().toString(), entry.getValue().getString()));
        }
        return new EventHook(eventType, enabled, method, address, dataType, headers);
    }

    @Override
    public void serialize(Type type, @Nullable EventHook obj, ConfigurationNode node)
            throws SerializationException {
        if (obj == null) {
            return;
        }

        node.node("event").set(obj.getEventType());
        node.node("enabled").set(obj.isEnabled());
        node.node("method").set(obj.getMethod());
        node.node("address").set(obj.getAddress());
        node.node("type").set(obj.getDataType());

        var headers = node.node("headers");
        for (var header : obj.getHeaders()) {
            headers.node(header.name()).set(header.value());
        }
    }
}
