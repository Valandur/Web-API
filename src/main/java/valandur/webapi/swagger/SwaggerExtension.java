package valandur.webapi.swagger;

import io.swagger.jersey.SwaggerJersey2Jaxrs;
import io.swagger.models.Operation;
import io.swagger.models.RefResponse;
import io.swagger.models.Response;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.parameters.RefParameter;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import valandur.webapi.api.exceptions.NotImplementedException;
import valandur.webapi.api.servlet.Permission;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class SwaggerExtension extends SwaggerJersey2Jaxrs {

    @Override
    public void decorateOperation(Operation operation, Method method,
                                  Iterator<io.swagger.jaxrs.ext.SwaggerExtension> chain) {
        // Automatically add query params
        operation.addParameter(new RefParameter("details"));
        operation.addParameter(new RefParameter("accept"));
        operation.addParameter(new RefParameter("pretty"));

        // Automatically add 500 as a possible response
        operation.addResponse("500", new RefResponse("500"));

        // Automatically add error codes depending on thrown exceptions
        for (Class<?> execClass : method.getExceptionTypes()) {
            if (BadRequestException.class.isAssignableFrom(execClass)) {
                operation.addResponse("400", new RefResponse("400"));
            }
            if (NotFoundException.class.isAssignableFrom(execClass)) {
                operation.addResponse("404", new RefResponse("404"));
            }
            if (NotImplementedException.class.isAssignableFrom(execClass)) {
                operation.addResponse("501", new RefResponse("501"));
            }
        }

        Permission[] perms = method.getAnnotationsByType(Permission.class);
        if (perms.length > 0) {
            // Automatically add 401 & 403 as a possible response
            operation.addResponse("401", new RefResponse("401"));
            operation.addResponse("403", new RefResponse("403"));

            // Automatically add required permission notes if we have a @Permission annotation
            Path path = method.getDeclaringClass().getAnnotation(Path.class);
            String prefix = path != null ? path.value() + "." : "";

            StringBuilder permStr = new StringBuilder("  \n\n **Required permissions:**  \n\n");
            for (Permission perm : perms) {
                permStr.append("- **").append(prefix).append(String.join(".", perm.value())).append("**  \n");
            }

            operation.setDescription(operation.getDescription() + permStr.toString());

            // Add security definitions
            operation.addSecurity("ApiKeyHeader", new ArrayList<>());
            operation.addSecurity("ApiKeyQuery", new ArrayList<>());
        }
        super.decorateOperation(operation, method, chain);
    }
}
