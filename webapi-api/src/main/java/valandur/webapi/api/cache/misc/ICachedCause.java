package valandur.webapi.api.cache.misc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.event.cause.Cause;
import valandur.webapi.api.cache.ICachedObject;

import java.util.List;
import java.util.Map;

@ApiModel("Cause")
public interface ICachedCause extends ICachedObject<Cause> {

    @ApiModelProperty("The context surrounding the cause")
    Map<String, Object> getContext();

    @ApiModelProperty("The direct sources of this cause")
    List<Object> getCauses();
}
