package valandur.webapi.servlet.base;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation tells the Web-API that details should not be included for a certain endpoint, unless explicitly
 * requested
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ExplicitDetails {
    /**
     * Usually endpoints include all details of objects they send, but for lists that can contain a lot of objects
     * it can be beneficial to mark them so as not to include too much information in one response.
     * @return True if the details of the objects this endpoint should only be included when explicitly requested
     * by the user
     */
    boolean value() default true;
}
