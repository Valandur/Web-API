package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.BreedableData;
import valandur.webapi.cache.CachedObject;

public class CachedBreedableData extends CachedObject<BreedableData> {

    @JsonValue
    public boolean breedable;


    public CachedBreedableData(BreedableData value) {
        super(value);

        this.breedable = value.breedable().get();
    }
}
