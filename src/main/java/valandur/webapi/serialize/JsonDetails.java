package valandur.webapi.serialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation causes fields or methods to only be included in serialized responses if details were explicitly
 * requested while serializing the object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface JsonDetails {

    /**
     * Specifies if the annotated value should only be included in the response when details are requested.
     * @return True if the annotated value is only serialized when details where requested, false otherwise.
     */
    boolean value() default true;

    /**
     * Specifies whether the annotated value should always be serialized without details, even if they were requested.
     * @return True if the annotated value should always be serialized without details, even if they were requested,
     * false otherwise.
     */
    boolean simple() default false;
}
