package valandur.webapi.serialize;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class AnnotationIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public Object findFilterId(Annotated a) {
        Object filter = super.findFilterId(a);
        return filter != null ? filter : BaseFilter.ID;
    }
}
