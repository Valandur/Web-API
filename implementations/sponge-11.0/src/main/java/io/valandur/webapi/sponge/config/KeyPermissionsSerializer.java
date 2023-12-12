package io.valandur.webapi.sponge.config;

import io.valandur.webapi.security.Access;
import io.valandur.webapi.security.KeyPermissions;
import java.lang.reflect.Type;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public class KeyPermissionsSerializer implements TypeSerializer<KeyPermissions> {

  @Override
  public KeyPermissions deserialize(Type type, ConfigurationNode node)
      throws SerializationException {
    var rateLimit = node.node("rateLimit").getInt(0);
    var access = node.node("access").get(Access.class);
    return new KeyPermissions(rateLimit, access);
  }

  @Override
  public void serialize(Type type, @Nullable KeyPermissions obj, ConfigurationNode node)
      throws SerializationException {
    if (obj == null) {
      return;
    }

    node.node("rateLimit").set(obj.rateLimit);
    node.node("access").set(obj.access);
  }
}
