package valandur.webapi.cache.misc;

import com.fasterxml.jackson.annotation.JsonValue;
import valandur.webapi.cache.CachedObject;

import java.time.Instant;

public class CachedInstant extends CachedObject<Instant> {

    @JsonValue
    public Long instant;


    public CachedInstant(Instant value) {
        super(value);

        this.instant = value.getEpochSecond();
    }
}
