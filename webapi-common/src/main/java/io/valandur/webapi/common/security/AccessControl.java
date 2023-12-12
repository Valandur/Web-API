package io.valandur.webapi.common.security;

import io.valandur.webapi.security.Access;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessControl {

  Access value() default Access.READ;
}
