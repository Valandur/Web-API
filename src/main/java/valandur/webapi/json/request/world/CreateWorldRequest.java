package valandur.webapi.json.request.world;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.DimensionType;

import java.util.Collection;
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
