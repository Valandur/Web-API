package valandur.webapi.api.cache.world;

import com.flowpowered.math.vector.Vector3d;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.world.WorldBorder;
import valandur.webapi.api.cache.ICachedObject;

@ApiModel("WorldBorder")
public interface ICachedWorldBorder extends ICachedObject<WorldBorder> {

    @ApiModelProperty("The center of the world border")
    Vector3d getCenter();

    double getDiameter();

    double getDamageAmount();

    double getDamageThreshold();

    double getNewDiameter();

    long getTimeRemaining();

    int getWarningDistance();

    int getWarningTime();
}
