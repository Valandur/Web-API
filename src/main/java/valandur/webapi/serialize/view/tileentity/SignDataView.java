package valandur.webapi.serialize.view.tileentity;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.text.Text;
import valandur.webapi.api.serialize.BaseView;

import java.util.List;
import java.util.stream.Collectors;

public class SignDataView extends BaseView<SignData> {

    @JsonValue
    public List<String> lines;


    public SignDataView(SignData value) {
        super(value);

        this.lines = value.lines().get().stream().map(Text::toPlain).collect(Collectors.toList());
    }
}
