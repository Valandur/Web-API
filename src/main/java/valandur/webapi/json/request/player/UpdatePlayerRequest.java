package valandur.webapi.json.request.player;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import valandur.webapi.json.request.entity.UpdateEntityRequest;

@JsonDeserialize
public class UpdatePlayerRequest extends UpdateEntityRequest {

    @JsonDeserialize
    private Integer foodLevel;
    public Integer getFoodLevel() {
        return foodLevel;
    }

    @JsonDeserialize
    private Double saturation;
    public Double getSaturation() {
        return saturation;
    }

    @JsonDeserialize
    private Double exhaustion;
    public Double getExhaustion() {
        return exhaustion;
    }
}
