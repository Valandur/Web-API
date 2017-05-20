package valandur.webapi.json;

import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class WebAPIAnnotationIntrospector extends JacksonAnnotationIntrospector {

    private boolean includeAnyGetter = false;

    public WebAPIAnnotationIntrospector(boolean includeAnyGetter) {
        this.includeAnyGetter = includeAnyGetter;
    }

    @Override
    public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
        return includeAnyGetter && super.hasAnyGetterAnnotation(am);
    }
}
