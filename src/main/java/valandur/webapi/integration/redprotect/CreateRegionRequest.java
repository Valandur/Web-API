package valandur.webapi.integration.redprotect;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flowpowered.math.vector.Vector3d;

@JsonDeserialize
public class CreateRegionRequest extends BaseRegionRequest {

    @JsonDeserialize
    private String name;
    public String getName() {
        return name;
    }

    @JsonDeserialize
    private Vector3d minPos;
    public Vector3d getMinLoc() {
        return minPos;
    }

    @JsonDeserialize
    private Vector3d maxPos;
    public Vector3d getMaxLoc() {
        return maxPos;
    }
}
