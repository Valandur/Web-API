package valandur.webapi.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import valandur.webapi.link.LinkType;

@ConfigSerializable
public class LinkConfig extends BaseConfig {

    @Setting(comment = "The type of link which is used to connect multiple servers.\n" +
            "Can be one of 'None', 'WebSocket', 'Redis' or 'RabbitMQ'")
    public LinkType type = LinkType.None;

    @Setting(comment = "The url which this plugin should connect to.")
    public String url = "";

    @Setting(comment = "The key that is used to identify this node. Make it long and secure!")
    public String privateKey = "";
}
