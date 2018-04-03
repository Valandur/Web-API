package valandur.webapi.cache.misc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.event.cause.Cause;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.misc.ICachedCause;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.util.Util;

import java.util.Map;

public class CachedCause extends CachedObject<Cause> implements ICachedCause {

    @Override
    @JsonValue
    @JsonDetails(false)
    public Map<String, Object> getData() {
        return super.getData();
    }

    public CachedCause(Cause cause) {
        super(cause);

        for (Map.Entry<String, Object> entry : cause.getNamedCauses().entrySet()) {
            String key = Util.lowerFirst(entry.getKey());
            data.put(key, WebAPI.getCacheService().asCachedObject(entry.getValue()));
        }
    }

    @Override
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public String getLink() {
        return null;
    }
}
