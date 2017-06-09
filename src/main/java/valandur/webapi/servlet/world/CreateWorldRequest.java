package valandur.webapi.servlet.world;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.difficulty.Difficulty;

import java.util.Collection;
import java.util.Optional;

@JsonDeserialize
public class CreateWorldRequest {

    @JsonDeserialize
    private String name;
    public String getName() {
        return name;
    }

    @JsonDeserialize
    private String dimension;
    public Optional<DimensionType> getDimensionType() {
        Collection<DimensionType> types = Sponge.getRegistry().getAllOf(DimensionType.class);
        return types.stream().filter(t -> t.getId().equalsIgnoreCase(dimension) || t.getName().equalsIgnoreCase(dimension)).findAny();
    }

    @JsonDeserialize
    private String generator;
    public Optional<GeneratorType> getGeneratorType() {
        Collection<GeneratorType> types = Sponge.getRegistry().getAllOf(GeneratorType.class);
        return types.stream().filter(g -> g.getId().equalsIgnoreCase(generator) || g.getName().equalsIgnoreCase(generator)).findAny();
    }

    @JsonDeserialize
    private Long seed;
    public Long getSeed() {
        return seed;
    }

    @JsonDeserialize
    private String gameMode;
    public Optional<GameMode> getGameMode() {
        Collection<GameMode> types = Sponge.getRegistry().getAllOf(GameMode.class);
        return types.stream().filter(g -> g.getId().equalsIgnoreCase(gameMode) || g.getName().equalsIgnoreCase(gameMode)).findAny();
    }

    @JsonDeserialize
    private String difficulty;
    public Optional<Difficulty> getDifficulty() {
        Collection<Difficulty> types = Sponge.getRegistry().getAllOf(Difficulty.class);
        return types.stream().filter(g -> g.getId().equalsIgnoreCase(difficulty) || g.getName().equalsIgnoreCase(difficulty)).findAny();
    }

    @JsonDeserialize
    private Boolean loadOnStartup;
    public Boolean doesLoadOnStartup() {
        return loadOnStartup;
    }

    @JsonDeserialize
    private Boolean keepSpawnLoaded;
    public Boolean doesKeepSpawnLoaded() {
        return keepSpawnLoaded;
    }

    @JsonDeserialize
    private Boolean allowCommands;
    public Boolean doesAllowCommands() {
        return allowCommands;
    }

    @JsonDeserialize
    private Boolean generateBonusChest;
    public Boolean doesGenerateBonusChest() {
        return generateBonusChest;
    }

    @JsonDeserialize
    private Boolean usesMapFeatures;
    public Boolean doesUseMapFeatures() {
        return usesMapFeatures;
    }
}
