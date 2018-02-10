package valandur.webapi.serialize.view.permission;

import io.swagger.annotations.ApiModel;
import org.spongepowered.api.service.permission.Subject;
import valandur.webapi.api.serialize.BaseView;

import java.util.HashMap;
import java.util.Map;

@ApiModel("Subject")
public class SubjectView extends BaseView<Subject> {

    public String getId() {
        return value.getIdentifier();
    }

    public String getDisplayName() {
        return value.getFriendlyIdentifier().orElse(null);
    }

    public Map<String, Boolean> getPermissions() {
        Map<String, Boolean> res = new HashMap<>();
        for (Map<String, Boolean> map : value.getSubjectData().getAllPermissions().values()) {
            res.putAll(map);
        }
        return res;
    }

    public SubjectView(Subject value) {
        super(value);
    }
}
