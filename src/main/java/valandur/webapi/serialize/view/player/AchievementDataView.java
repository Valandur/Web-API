package valandur.webapi.serialize.view.player;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.AchievementData;
import org.spongepowered.api.statistic.achievement.Achievement;
import valandur.webapi.api.serialize.BaseView;

import java.util.Set;

public class AchievementDataView extends BaseView<AchievementData> {

    @JsonValue
    public Set<Achievement> achievements;


    public AchievementDataView(AchievementData value) {
        super(value);

        this.achievements = value.achievements().get();
    }
}
