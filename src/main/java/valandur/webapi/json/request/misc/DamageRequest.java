package valandur.webapi.json.request.misc;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.entity.damage.DamageType;

import java.util.Collection;
import java.util.Optional;

public class DamageRequest {

    @JsonDeserialize
    private Integer amount;
    public Integer getAmount() {
        return amount;
    }

    @JsonDeserialize
    private String type;
    public Optional<DamageType> getDamageType() {
        Collection<DamageType> types = Sponge.getRegistry().getAllOf(DamageType.class);
        return types.stream().filter(t -> t.getId().equalsIgnoreCase(type) || t.getName().equalsIgnoreCase(type)).findAny();
    }
}
