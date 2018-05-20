package valandur.webapi.cache.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowpowered.math.vector.Vector3d;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.api.world.storage.WorldProperties;
import valandur.webapi.cache.CachedObject;

@ApiModel("WorldBorder")
public class CachedWorldBorder extends CachedObject<WorldBorder> {

    private Vector3d center;
    @ApiModelProperty(value = "The center of the world border", required = true)
    public Vector3d getCenter() {
        return center;
    }

    private double diameter;
    @ApiModelProperty(value = "The diameter of the border", required = true)
    public double getDiameter() {
        return diameter;
    }

    private double damageAmount;
    @ApiModelProperty(value = "The amount of damage done to players outside the border", required = true)
    public double getDamageAmount() {
        return damageAmount;
    }

    private double damageThreshold;
    @ApiModelProperty(value = "The maximum amount of damage done to a player", required = true)
    public double getDamageThreshold() {
        return damageThreshold;
    }

    private double newDiameter;
    @ApiModelProperty(value = "The new diameter of the border, in case it is collapsing/expanding", required = true)
    public double getNewDiameter() {
        return newDiameter;
    }

    private long timeRemaining;
    @ApiModelProperty(value = "The time remaining until the border reaches it's new size", required = true)
    public long getTimeRemaining() {
        return timeRemaining;
    }

    private int warningDistance;
    @ApiModelProperty(value = "The amount of distance from the border a player will receive a warning at", required = true)
    public int getWarningDistance() {
        return warningDistance;
    }

    private int warningTime;
    @ApiModelProperty(value = "The amount of time near a border before a warning is shown for players", required = true)
    public int getWarningTime() {
        return warningTime;
    }


    public CachedWorldBorder(WorldBorder border) {
        super(null);

        this.center = border.getCenter().clone();
        this.diameter = border.getDiameter();
        this.damageAmount = border.getDamageAmount();
        this.damageThreshold = border.getDamageThreshold();
        this.newDiameter = border.getNewDiameter();
        this.timeRemaining = border.getTimeRemaining();
        this.warningDistance = border.getWarningDistance();
        this.warningTime = border.getWarningTime();
    }
    public CachedWorldBorder(WorldProperties properties) {
        super(null);

        this.center = properties.getWorldBorderCenter().clone();
        this.diameter = properties.getWorldBorderDiameter();
        this.damageAmount = properties.getWorldBorderDamageAmount();
        this.damageThreshold = properties.getWorldBorderDamageThreshold();
        this.newDiameter = properties.getWorldBorderTargetDiameter();
        this.timeRemaining = properties.getWorldBorderTimeRemaining();
        this.warningDistance = properties.getWorldBorderWarningDistance();
        this.warningTime = properties.getWorldBorderWarningTime();
    }

    @Override
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public String getLink() {
        return null;
    }
}
