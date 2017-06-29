package valandur.webapi.json.serializer.user;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import valandur.webapi.api.json.WebAPISerializer;
import valandur.webapi.user.UserPermission;

import java.io.IOException;

public class UserPermissionSerializer extends WebAPISerializer<UserPermission> {
    @Override
    public void serialize(UserPermission value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        writeField(provider, "username", value.getUsername());
        writeField(provider, "permissions", value.getPermissions());
        gen.writeEndObject();
    }
}
