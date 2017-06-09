package valandur.webapi.servlet.world;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.difficulty.Difficulty;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@JsonDeserialize
public class CreateWorldRequest extends BaseWorldRequest {
    @JsonDeserialize
    private String dimension;
    public Optional<DimensionType> getDimensionType() {
        Collection<DimensionType> types = Sponge.getRegistry().getAllOf(DimensionType.class);
        return types.stream().filter(t -> t.getId().equalsIgnoreCase(dimension) || t.getName().equalsIgnoreCase(dimension)).findAny();
    }

    @JsonDeserialize
    private Boolean generateBonusChest;
    public Boolean doesGenerateBonusChest() {
        return generateBonusChest;
    }
}
