package valandur.webapi.servlet.base;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation tells the Web-API which permissions are required to access the annotated endpoint
 */
@Retention(RUNTIME)
@Target(METHOD)
@Repeatable(Permissions.class)
public @interface Permission {
    /**
     * The permissions required to access this endpoint. An empty String (the default) means that no permissions are
     * required. These permissions are relative to the servlet path, which serves as the root permissions entry.
     * @return The permissions required for this endpoint.
     */
    String[] value() default {};

    /**
     * Specify whether this permission is automatically checked against the user's permissions. This should usually
     * be set to true, unless this permissions is checked manually and only used for the docs.
     * @return True if the permission is automatically checked, false otherwise.
     */
    boolean autoCheck() default true;
}
