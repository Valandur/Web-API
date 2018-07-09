package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.ColoredData;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedColor;

public class CachedColoredData extends CachedObject<ColoredData> {

    @JsonValue
    public CachedColor color;


    public CachedColoredData(ColoredData value) {
        super(value);

        this.color = new CachedColor(value.color().get());
    }
}
