package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedText;

public class CachedDisplayNameData extends CachedObject<DisplayNameData> {

    @JsonValue
    public CachedText name;


    public CachedDisplayNameData(DisplayNameData value) {
        super(value);

        this.name = new CachedText(value.displayName().get());
    }
}
