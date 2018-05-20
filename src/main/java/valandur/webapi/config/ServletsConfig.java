package valandur.webapi.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ServletsConfig extends BaseConfig {

    @Setting
    public boolean Block = true;

    @Setting
    public boolean Chunk = true;

    @Setting
    public boolean Cmd = true;

    @Setting
    public boolean Economy = true;

    @Setting
    public boolean Entity = true;

    @Setting
    public boolean History = true;

    @Setting
    public boolean Info = true;

    @Setting
    public boolean InteractiveMessage = true;

    @Setting
    public boolean Map = true;

    @Setting
    public boolean Permission = true;

    @Setting
    public boolean Player = true;

    @Setting
    public boolean Plugin = true;

    @Setting
    public boolean Recipe = true;

    @Setting
    public boolean Registry = true;

    @Setting
    public boolean Server = true;

    @Setting
    public boolean TileEntity = true;

    @Setting
    public boolean User = true;

    @Setting
    public boolean World = true;

    @Setting
    public ServletsIntegrationConfig integrations = new ServletsIntegrationConfig();


    @ConfigSerializable
    public static class ServletsIntegrationConfig {

        @Setting
        public boolean ActiveTime = true;

        @Setting
        public boolean CmdScheduler = true;

        @Setting
        public boolean HuskyCrates = true;

        @Setting
        public boolean MMCRestrict = true;

        @Setting
        public boolean MMCTickets = true;

        @Setting
        public boolean Nucleus = true;

        @Setting
        public boolean RedProtect = true;

        @Setting
        public boolean UniversalMarket = true;

        @Setting
        public boolean WebBooks = true;
    }
}
