package valandur.webapi.serialize;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

public class SerializationFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.register(SerializationProvider.class, MessageBodyReader.class, MessageBodyWriter.class);
        return true;
    }
}
