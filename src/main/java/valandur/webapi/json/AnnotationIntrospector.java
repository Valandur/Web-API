package valandur.webapi.json;

import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class AnnotationIntrospector extends JacksonAnnotationIntrospector {

    private boolean includeAnyGetter = false;

    public AnnotationIntrospector(boolean includeAnyGetter) {
        this.includeAnyGetter = includeAnyGetter;
    }

    @Override
    public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
        return includeAnyGetter && super.hasAnyGetterAnnotation(am);
    }
}
