package valandur.webapi.api.cache.misc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.event.cause.Cause;
import valandur.webapi.api.cache.ICachedObject;

import java.util.List;
import java.util.Map;

@ApiModel("Cause")
public interface ICachedCause extends ICachedObject<Cause> {

    @ApiModelProperty(value = "The context surrounding the cause", required = true)
    Map<String, Object> getContext();

    @ApiModelProperty(value = "The direct sources of this cause", required = true)
    List<Object> getCauses();
}
