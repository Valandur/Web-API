package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.PlayingData;
import valandur.webapi.serialize.BaseView;

public class PlayingDataView extends BaseView<PlayingData> {

    @JsonValue
    public boolean playing;


    public PlayingDataView(PlayingData value) {
        super(value);

        this.playing = value.playing().get();
    }
}
