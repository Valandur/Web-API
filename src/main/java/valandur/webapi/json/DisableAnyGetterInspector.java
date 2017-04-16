package valandur.webapi.json;

import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class DisableAnyGetterInspector extends JacksonAnnotationIntrospector {
    @Override
    public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
        return false;
    }
}
