package valandur.webapi.api.cache.world;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.api.world.storage.WorldProperties;
import valandur.webapi.api.cache.CachedObject;

public class CachedWorldBorder extends CachedObject {

    private Vector3d center;
    public Vector3d getCenter() {
        return center;
    }

    private double diameter;
    public double getDiameter() {
        return diameter;
    }

    private double damageAmount;
    public double getDamageAmount() {
        return damageAmount;
    }

    private double damageThreshold;
    public double getDamageThreshold() {
        return damageThreshold;
    }

    private double newDiameter;
    public double getNewDiameter() {
        return newDiameter;
    }

    private long timeRemaining;
    public long getTimeRemaining() {
        return timeRemaining;
    }

    private int warningDistance;
    public int getWarningDistance() {
        return warningDistance;
    }

    private int warningTime;
    public int getWarningTime() {
        return warningTime;
    }


    public CachedWorldBorder(WorldBorder border) {
        super(border);

        this.center = border.getCenter();
        this.diameter = border.getDiameter();
        this.damageAmount = border.getDamageAmount();
        this.damageThreshold = border.getDamageThreshold();
        this.newDiameter = border.getNewDiameter();
        this.timeRemaining = border.getTimeRemaining();
        this.warningDistance = border.getWarningDistance();
        this.warningTime = border.getWarningTime();
    }

    public CachedWorldBorder(WorldProperties properties) {
        super(properties);

        this.center = properties.getWorldBorderCenter();
        this.diameter = properties.getWorldBorderDiameter();
        this.damageAmount = properties.getWorldBorderDamageAmount();
        this.damageThreshold = properties.getWorldBorderDamageThreshold();
        this.newDiameter = properties.getWorldBorderTargetDiameter();
        this.timeRemaining = properties.getWorldBorderTimeRemaining();
        this.warningDistance = properties.getWorldBorderWarningDistance();
        this.warningTime = properties.getWorldBorderWarningTime();
    }
}
