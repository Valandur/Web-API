package valandur.webapi.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This marks a class as a Web-API servlet, which serves certain endpoints under the same base path. The class must
 * have this annotation and also derive from {@link valandur.webapi.api.servlet.WebAPIBaseServlet}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WebAPIServlet {

    /**
     * The base path this servlet will be available at. All paths specified for methods in the {@link WebAPIEndpoint}
     * annotation wil be relative to this path.
     * @return The base path of this servlet
     */
    String basePath();
}
