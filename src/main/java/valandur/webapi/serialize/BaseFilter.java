package valandur.webapi.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import valandur.webapi.WebAPI;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.api.util.TreeNode;
import valandur.webapi.security.PermissionService;

import java.util.ArrayList;

public class BaseFilter extends SimpleBeanPropertyFilter {

    public static String ID = "WEBAPI-BASE-FILTER";

    private PermissionService permissionService;
    private TreeNode<String, Boolean> perms;
    private ArrayList<String> path;
    private boolean details;


    public BaseFilter(boolean details, TreeNode<String, Boolean> perms) {
        this.permissionService = WebAPI.getPermissionService();
        this.details = details;
        this.path = new ArrayList<>();
        this.perms = perms;
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer)
            throws Exception {
        String key = writer.getName();
        boolean prevDetails = details;

        // Check if we have to skip the field because it is marked as "details" and details is set to false
        // or if we have to turn details off temporarily in case the field is marked as "simple"
        JsonDetails det = writer.getAnnotation(JsonDetails.class);
        if (det != null) {
            if (!details && det.value()) {
                return;
            }

            if (det.simple()) {
                details = false;
            }
        }

        // Add our object to the path
        path.add(key);

        // Check if the permission service allows access to our path
        // If yes then we want to serialize the rest of our object
        if (permissionService.permits(perms, path)) {
            super.serializeAsField(pojo, jgen, provider, writer);
        }

        // Reset path and details after our object is done
        path.remove(key);
        details = prevDetails;
    }
}
