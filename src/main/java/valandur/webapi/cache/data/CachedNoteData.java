package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.tileentity.NoteData;
import org.spongepowered.api.data.type.NotePitch;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;

public class CachedNoteData extends CachedObject<NoteData> {

    @JsonValue
    public CachedCatalogType<NotePitch> note;


    public CachedNoteData(NoteData value) {
        super(value);

        this.note = new CachedCatalogType<>(value.note().get());
    }
}
