package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.AgentData;
import valandur.webapi.cache.CachedObject;

public class CachedAgentData extends CachedObject<AgentData> {

    @JsonValue
    public boolean ai;


    public CachedAgentData(AgentData value) {
        super(value);

        this.ai = value.aiEnabled().get();
    }
}
