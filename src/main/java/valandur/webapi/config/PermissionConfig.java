package valandur.webapi.config;

import com.google.common.collect.Lists;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import valandur.webapi.api.security.IPermissionService;
import valandur.webapi.security.PermissionStruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class PermissionConfig extends BaseConfig {

    @Setting(comment = "Set this to true to enable the whitelist, false to turn it off")
    public boolean useWhitelist = true;

    @Setting(comment = "Add IP address that are allowed to connect to the Web-API to this list")
    public List<String> whitelist = Lists.newArrayList("127.0.0.1", "::1");

    @Setting(comment = "Set this to true to enable the blacklist, false to turn it off")
    public boolean useBlacklist = false;

    @Setting(comment = "Add the IP addresses that are NOT allowed to connect to the Web-API to this list")
    public List<String> blacklist = new ArrayList<>();

    @Setting(comment = "The servers which are allowed to pass the X-Forwarded-For header indicating that they are\n" +
            "forwarding a request for a client and are themselves a proxy. You can use IP addresses or\n" +
            "CIDR notation IP blocks (e.g. \"192.168.0.0/18\")")
    public List<String> allowedProxies = Lists.newArrayList("127.0.0.1", "::1");

    @Setting(comment = "The access control origin header that is sent with each request.\n" +
            "This is useful if you want to prevent CORS,\n" +
            "but remember that it must at least include the server where the AdminPanel is running")
    public String accessControlOrigin = "*";

    @Setting(value = "default", comment = "These are the default permissions that a client without a key receives")
    public PermissionStruct def = new PermissionStruct(IPermissionService.emptyNode(), 1);

    @Setting(comment = "This is a map of keys, defining which keys give access to which endpoints.")
    public Map<String, PermissionStruct> keys = new HashMap<>();
}
