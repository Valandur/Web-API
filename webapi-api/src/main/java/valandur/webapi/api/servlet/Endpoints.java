package valandur.webapi.api.servlet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Endpoints {

    /**
     * The list of annotations belonging to this method
     * @return A list of endpoint annotation which are multiple ways to reach this endpoint
     */
    Endpoint[] value();
}
