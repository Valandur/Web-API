package valandur.webapi.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import valandur.webapi.WebAPI;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.api.permission.IPermissionService;
import valandur.webapi.api.util.TreeNode;
import valandur.webapi.permission.PermissionService;

import java.util.ArrayList;

public class BaseFilter extends SimpleBeanPropertyFilter {

    public static String ID = "WEBAPI-BASE-FILTER";

    private PermissionService permissionService;
    private TreeNode<String, Boolean> perms = IPermissionService.emptyNode();
    private ArrayList<String> path = new ArrayList<>();
    private boolean details = false;


    public BaseFilter(boolean details, TreeNode<String, Boolean> perms) {
        this.permissionService = WebAPI.getPermissionService();
        this.details = details;
        this.path = new ArrayList<>();
        this.perms = perms;
    }

    @Override
    public void serializeAsElement(Object elementValue, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        JsonSerializer ser = provider.findValueSerializer(writer.getType());
        WebAPI.getLogger().info(writer.getName() + ": " + (ser != null ? ser.getClass().getName() : ""));
        super.serializeAsElement(elementValue, jgen, provider, writer);
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
        String key = writer.getName();
        boolean prevDetails = details;

        JsonDetails det = writer.getAnnotation(JsonDetails.class);
        if (det != null) {
            if (!details && det.value()) {
                return;
            }

            if (det.simple()) {
                details = false;
            }
        }

        path.add(key);

        if (!permissionService.permits(perms, path)) {
            path.remove(key);
            return;
        }

        super.serializeAsField(pojo, jgen, provider, writer);

        details = prevDetails;
    }
}
