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

    @JsonDeserialize
    private Integer totalExperience;
    public Integer getTotalExperience() {
        return totalExperience;
    }

    @JsonDeserialize
    private Integer level;
    public Integer getLevel() {
        return level;
    }

    @JsonDeserialize
    private Integer experienceSinceLevel;
    public Integer getExperienceSinceLevel() {
        return experienceSinceLevel;
    }
}
