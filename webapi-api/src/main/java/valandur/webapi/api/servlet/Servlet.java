package valandur.webapi.api.servlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This marks a class as a Web-API servlet, which serves certain endpoints under the same base path. The class must
 * have this annotation and also derive from {@link BaseServlet}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Servlet {

    /**
     * The base path this servlet will be available at. All paths specified for methods in the {@link Endpoint}
     * annotation wil be relative to this path.
     * @return The base path of this servlet
     */
    String basePath();
}
