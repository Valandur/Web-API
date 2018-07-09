package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.SkinData;
import valandur.webapi.cache.CachedObject;

import java.util.UUID;

public class CachedSkinData extends CachedObject<SkinData> {

    @JsonValue
    public UUID uuid;


    public CachedSkinData(SkinData value) {
        super(value);

        this.uuid = value.skinUniqueId().get();
    }
}
