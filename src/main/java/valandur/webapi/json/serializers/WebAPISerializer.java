package valandur.webapi.json.serializers;

import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.jodah.typetools.TypeResolver;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.util.Tristate;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.misc.TreeNode;
import valandur.webapi.permission.Permissions;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class WebAPISerializer<T> extends StdSerializer<T> {

    private Class clazz;
    public Class getHandledClass() {
        return clazz;
    }

    public WebAPISerializer() {
        this(null);
        this.clazz = TypeResolver.resolveRawArgument(WebAPISerializer.class, getClass());
    }
    private WebAPISerializer(Class<T> t) {
        super(t);
    }

    protected void writeData(SerializerProvider provider, DataHolder holder) throws IOException {
        for (Map.Entry<String, Class> entry : JsonConverter.getSupportedData().entrySet()) {
            Optional<?> m = holder.get(entry.getValue());

            if (!m.isPresent())
                continue;

            writeField(provider, entry.getKey(), m.get());
        }
    }

    protected void writeField(SerializerProvider provider, String key, Object value) throws IOException {
        writeField(provider, key, value, Tristate.UNDEFINED);
    }
    protected void writeField(SerializerProvider provider, String key, Object value, Tristate details) throws IOException {
        TreeNode<String, Boolean> incs = (TreeNode<String, Boolean>)provider.getAttribute("includes");
        List<String> parents = (List<String>)provider.getAttribute("parents");

        parents.add(key);

        AtomicBoolean currDetails = (AtomicBoolean)provider.getAttribute("details");
        boolean prevDetails = currDetails.get();
        if (details == Tristate.TRUE) {
            currDetails.set(true);
        } else if (details == Tristate.FALSE) {
            currDetails.set(false);
        }

        if (!Permissions.permits(incs, parents)) {
            parents.remove(key);
            if (details != Tristate.UNDEFINED) {
                currDetails.set(prevDetails);
            }
            return;
        }

        provider.getGenerator().writeObjectField(key, value);

        if (details != Tristate.UNDEFINED) {
            currDetails.set(prevDetails);
        }

        parents.remove(key);
    }

    protected void writeValue(SerializerProvider provider, Object value) throws IOException {
        TreeNode<String, Boolean> incs = (TreeNode<String, Boolean>)provider.getAttribute("includes");
        List<String> parents = (List<String>)provider.getAttribute("parents");

        if (!Permissions.permits(incs, parents)) {
            return;
        }

        provider.getGenerator().writeObject(value);
    }
}
