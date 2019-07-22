package valandur.webapi.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class BlockConfig extends BaseConfig {

    @Setting(comment = "This controls the maximum amount of blocks a client can get\n" +
            "in one Web-API call")
    public int maxBlockGetSize = 1000000;

    @Setting(comment = "This is the maximum amount of blocks that a client can change\n" +
            "in one Web-API call. Please note that not all blocks are changed\n" +
            "at once (see below)")
    public int maxBlockUpdateSize = 1000000;

    @Setting(comment = "The maximum number of blocks that are changed per second during\n" +
            "a block update (related to the setting above)")
    public int maxBlocksPerSecond = 1000;
}
