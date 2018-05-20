package valandur.webapi.serialize.view.player;

import org.spongepowered.api.statistic.achievement.Achievement;
import valandur.webapi.serialize.BaseView;

public class AchievementView extends BaseView<Achievement> {

    public String id;
    public String name;
    public String description;


    public AchievementView(Achievement value) {
        super(value);

        this.id = value.getId();
        this.name = value.getName();
        this.description = value.getDescription().get();
    }
}
