package valandur.webapi.serialize;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class AnnotationIntrospector extends JacksonAnnotationIntrospector {

    private boolean details = true;

    public AnnotationIntrospector(boolean details) {
        this.details = details;
    }

    @Override
    public Boolean hasAnyGetter(Annotated a) {
        Boolean hasAnyGetter = super.hasAnyGetter(a);
        return details && hasAnyGetter != null && hasAnyGetter;
    }

    @Override
    public Boolean hasAsValue(Annotated a) {
        Boolean value = super.hasAsValue(a);
        return value;
    }

    @Override
    public Object findFilterId(Annotated a) {
        Object filter = super.findFilterId(a);
        return filter != null ? filter : BaseFilter.ID;
    }
}
