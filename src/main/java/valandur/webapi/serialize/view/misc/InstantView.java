package valandur.webapi.serialize.view.misc;

import com.fasterxml.jackson.annotation.JsonValue;
import valandur.webapi.api.serialize.BaseView;

import java.time.Instant;

public class InstantView extends BaseView<Instant> {

    @JsonValue
    public Long instant;


    public InstantView(Instant value) {
        super(value);

        this.instant = value.getEpochSecond();
    }
}
