package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.item.GenerationData;
import valandur.webapi.cache.CachedObject;

public class CachedGenerationData extends CachedObject<GenerationData> {

    @JsonValue
    public int generation;


    public CachedGenerationData(GenerationData value) {
        super(value);

        this.generation = value.generation().get();
    }
}
