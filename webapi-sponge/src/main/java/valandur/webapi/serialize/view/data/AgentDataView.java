package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.AgentData;
import valandur.webapi.serialize.BaseView;

public class AgentDataView extends BaseView<AgentData> {

    @JsonValue
    public boolean ai;


    public AgentDataView(AgentData value) {
        super(value);

        this.ai = value.aiEnabled().get();
    }
}
