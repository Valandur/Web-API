package valandur.webapi.json.serializer.user;

import valandur.webapi.api.json.WebAPIBaseSerializer;
import valandur.webapi.user.UserPermission;

import java.io.IOException;

public class UserPermissionSerializer extends WebAPIBaseSerializer<UserPermission> {
    @Override
    public void serialize(UserPermission value) throws IOException {
        writeStartObject();
        writeField("username", value.getUsername());
        writeField("permissions", value.getPermissions());
        writeEndObject();
    }
}
