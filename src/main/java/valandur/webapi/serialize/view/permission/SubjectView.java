package valandur.webapi.serialize.view.permission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.service.permission.Subject;
import valandur.webapi.api.serialize.BaseView;
import valandur.webapi.api.serialize.JsonDetails;

import java.util.HashMap;
import java.util.Map;

@ApiModel("Subject")
public class SubjectView extends BaseView<Subject> {

    @ApiModelProperty(value = "The unique id of this subject", required = true)
    public String getId() {
        return value.getIdentifier();
    }

    @ApiModelProperty("The friendly id of this subject (often a 'name'")
    public String getFriendlyId() {
        return value.getFriendlyIdentifier().orElse(null);
    }

    @JsonDetails
    @ApiModelProperty(value = "The permissions that are assigned to this subject")
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
