package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.tileentity.NoteData;
import org.spongepowered.api.data.type.NotePitch;
import valandur.webapi.serialize.BaseView;

public class NoteDataView extends BaseView<NoteData> {

    @JsonValue
    public NotePitch note;


    public NoteDataView(NoteData value) {
        super(value);

        this.note = value.note().get();
    }
}
