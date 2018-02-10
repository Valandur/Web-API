package valandur.webapi.serialize.view.permission;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.Subject;
import valandur.webapi.api.serialize.BaseView;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SubjectView extends BaseView<Subject> {

    public String getIdentifier() {
        return value.getIdentifier();
    }

    public String getFriendlyIdentifier() {
        return value.getFriendlyIdentifier().orElse(null);
    }

    public List<GrantedPermission> getAllPermissions() {
        return value.getSubjectData().getAllPermissions().entrySet().stream()
                .map(e -> new GrantedPermission(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public SubjectView(Subject value) {
        super(value);
    }


    public static class GrantedPermission {
        public Set<Context> contexts;
        public Map<String, Boolean> permissions;

        public GrantedPermission(Set<Context> contexts, Map<String, Boolean> permissions) {
            this.contexts = contexts;
            this.permissions = permissions;
        }
    }
}
