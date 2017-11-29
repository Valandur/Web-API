package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.item.AuthorData;
import org.spongepowered.api.text.Text;
import valandur.webapi.api.serialize.BaseView;

public class AuthorDataView extends BaseView<AuthorData> {

    @JsonValue
    public Text author;


    public AuthorDataView(AuthorData value) {
        super(value);

        this.author = value.author().get();
    }
}
