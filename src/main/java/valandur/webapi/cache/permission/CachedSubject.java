package valandur.webapi.cache.permission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.service.permission.Subject;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.serialize.JsonDetails;

import java.util.HashMap;
import java.util.Map;

@ApiModel("Subject")
public class CachedSubject extends CachedObject<Subject> {

    private String id;
    @ApiModelProperty(value = "The unique id of this subject", required = true)
    public String getId() {
        return id;
    }

    private String friendlyId;
    @ApiModelProperty("The friendly id of this subject (often a 'name'")
    public String getFriendlyId() {
        return friendlyId;
    }

    private Map<String, Boolean> permissions;
    @JsonDetails
    @ApiModelProperty(value = "The permissions that are assigned to this subject")
    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    public CachedSubject(Subject value) {
        super(value);

        this.id = value.getIdentifier();
        this.friendlyId = value.getFriendlyIdentifier().orElse(null);
        Map<String, Boolean> perms = new HashMap<>();
        for (Map<String, Boolean> map : value.getSubjectData().getAllPermissions().values()) {
            perms.putAll(map);
        }
        this.permissions = perms;
    }
}
