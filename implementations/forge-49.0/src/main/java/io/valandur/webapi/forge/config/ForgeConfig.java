package io.valandur.webapi.forge.config;

import io.valandur.webapi.Config;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public abstract class ForgeConfig implements Config {

    protected final String name;

    protected final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    protected ForgeConfigSpec spec = null;

    public ForgeConfig(String name) {
        this.name = name;
    }

    protected void build() {
        // We wait with building the spec until here because otherwise the child classes don't get a chance to use the builder
        spec = BUILDER.build();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, spec, name);
    }

    @Override
    public void save() throws Exception {
        spec.save();
    }

    @Override
    public void load() throws Exception {
    }
}
