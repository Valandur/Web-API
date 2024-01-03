package io.valandur.webapi.forge.info;

import io.valandur.webapi.forge.config.ForgeConfig;
import io.valandur.webapi.info.InfoConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public class ForgeInfoConfig extends ForgeConfig implements InfoConfig {

    private final ForgeConfigSpec.IntValue INTERVAL = BUILDER
            .comment("The interval (in seconds) at which the server stats are collected. Zero (0) to disable.")
            .defineInRange("statsIntervalSeconds", defaultStatsIntervalSeconds, 0, Integer.MAX_VALUE);

    private final ForgeConfigSpec.IntValue MAX_ENTRIES = BUILDER
            .comment("The maximum number of stats entries that are collected over time. When full old entries are deleted.")
            .defineInRange("maxStatsEntries", defaultMaxStatsEntries, 0, Integer.MAX_VALUE);

    public ForgeInfoConfig() {
        super("info.toml");

        build();
    }

    @Override
    public int getStatsIntervalSeconds() {
        return INTERVAL.get();
    }

    @Override
    public int getMaxStatsEntries() {
        return MAX_ENTRIES.get();
    }
}
