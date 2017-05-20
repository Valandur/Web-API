package valandur.webapi.cache.world;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.world.WorldBorder;
import valandur.webapi.cache.CachedObject;

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
}
