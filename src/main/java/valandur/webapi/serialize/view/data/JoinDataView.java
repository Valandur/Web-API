package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import valandur.webapi.api.serialize.BaseView;

import java.time.Instant;

@ApiModel("JoinData")
public class JoinDataView extends BaseView<JoinData> {

    @ApiModelProperty(value = "The first time this entity joined the server", required = true)
    public Instant first;

    @ApiModelProperty(value = "The most recent time this entity joined the server", required = true)
    public Instant last;


    public JoinDataView(JoinData value) {
        super(value);

        this.first = value.firstPlayed().get();
        this.last = value.lastPlayed().get();
    }
}
