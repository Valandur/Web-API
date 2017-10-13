package valandur.webapi.serialize.view.entity;

import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.Profession;
import valandur.webapi.api.serialize.BaseView;

public class CareerView extends BaseView<Career> {

    public String id;
    public String name;
    public Profession profession;


    public CareerView(Career value) {
        super(value);

        this.id = value.getId();
        this.name = value.getTranslation().get();
        this.profession = value.getProfession();
    }
}
