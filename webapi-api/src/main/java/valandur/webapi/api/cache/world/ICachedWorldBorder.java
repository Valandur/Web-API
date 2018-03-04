package valandur.webapi.api.cache.world;

import com.flowpowered.math.vector.Vector3d;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.world.WorldBorder;
import valandur.webapi.api.cache.ICachedObject;

@ApiModel("WorldBorder")
public interface ICachedWorldBorder extends ICachedObject<WorldBorder> {

    @ApiModelProperty(value = "The center of the world border", required = true)
    Vector3d getCenter();

    @ApiModelProperty(value = "The diameter of the border", required = true)
    double getDiameter();

    @ApiModelProperty(value = "The amount of damage done to players outside the border", required = true)
    double getDamageAmount();

    @ApiModelProperty(value = "The maximum amount of damage done to a player", required = true)
    double getDamageThreshold();

    @ApiModelProperty(value = "The new diameter of the border, in case it is collapsing/expanding", required = true)
    double getNewDiameter();

    @ApiModelProperty(value = "The time remaining until the border reaches it's new size", required = true)
    long getTimeRemaining();

    @ApiModelProperty(value = "The amount of distance from the border a player will receive a warning at", required = true)
    int getWarningDistance();

    @ApiModelProperty(value = "The amount of time near a border before a warning is shown for players", required = true)
    int getWarningTime();
}
