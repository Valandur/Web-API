package valandur.webapi.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import valandur.webapi.user.UserPermissionStruct;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class UserConfig extends BaseConfig {

    @Setting
    public Map<String, UserPermissionStruct> users = new HashMap<>();
}
