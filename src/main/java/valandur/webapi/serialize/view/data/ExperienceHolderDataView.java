package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.entity.ExperienceHolderData;
import valandur.webapi.api.serialize.BaseView;

public class ExperienceHolderDataView extends BaseView<ExperienceHolderData> {

    public int level;
    public int experience;
    public int totalExperience;


    public ExperienceHolderDataView(ExperienceHolderData value) {
        super(value);

        this.level = value.level().get();
        this.experience = value.experienceSinceLevel().get();
        this.totalExperience = value.totalExperience().get();
    }
}
