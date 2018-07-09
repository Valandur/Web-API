package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.DecayableData;
import valandur.webapi.cache.CachedObject;

public class CachedDecayableData extends CachedObject<DecayableData> {

    @JsonValue
    public boolean decayable;


    public CachedDecayableData(DecayableData value) {
        super(value);

        this.decayable = value.decayable().get();
    }
}
