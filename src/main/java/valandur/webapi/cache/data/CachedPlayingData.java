package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.PlayingData;
import valandur.webapi.cache.CachedObject;

public class CachedPlayingData extends CachedObject<PlayingData> {

    @JsonValue
    public boolean playing;


    public CachedPlayingData(PlayingData value) {
        super(value);

        this.playing = value.playing().get();
    }
}
