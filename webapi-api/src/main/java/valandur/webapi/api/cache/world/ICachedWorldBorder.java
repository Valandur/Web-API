package valandur.webapi.api.cache.world;

import com.flowpowered.math.vector.Vector3d;
import valandur.webapi.api.cache.ICachedObject;

public interface ICachedWorldBorder extends ICachedObject {

    Vector3d getCenter();

    double getDiameter();

    double getDamageAmount();

    double getDamageThreshold();

    double getNewDiameter();

    long getTimeRemaining();

    int getWarningDistance();

    int getWarningTime();
}
