package valandur.webapi.serialize.view.player;

import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import valandur.webapi.api.serialize.BaseView;

import java.time.Instant;

public class JoinDataView extends BaseView<JoinData> {

    public Instant first;
    public Instant last;


    public JoinDataView(JoinData value) {
        super(value);

        this.first = value.firstPlayed().get();
        this.last = value.lastPlayed().get();
    }
}
