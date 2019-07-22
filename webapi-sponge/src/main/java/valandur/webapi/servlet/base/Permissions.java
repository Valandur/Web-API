package valandur.webapi.servlet.base;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * \@Permission annotations are repeatable, multiple ones means the endpoint requires multiple permissions
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Permissions {

    /**
     * The permissions required for the annotated endpoint.
     * @return An array of permissions required to access the annotated endpoint.
     */
    Permission[] value();
}
