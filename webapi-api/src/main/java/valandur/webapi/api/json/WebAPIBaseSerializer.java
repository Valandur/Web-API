package valandur.webapi.api.json;

import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.jodah.typetools.TypeResolver;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.util.Tristate;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.permission.IPermissionService;
import valandur.webapi.api.util.TreeNode;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The base serializer that other serializers must inherit from.
 * This serializer exposes the Web-API services as class variables for convenience.
 * @param <T>
 */
public abstract class WebAPIBaseSerializer<T> extends StdSerializer<T> {

    protected IJsonService jsonService;
    protected IPermissionService permissionService;

    private Class clazz;

    /**
     * Gets the class that is handled by this serializer.
     * @return The class handled by this serializer.
     */
    public Class getHandledClass() {
        return clazz;
    }


    public WebAPIBaseSerializer() {
        this(null);
        this.clazz = TypeResolver.resolveRawArgument(WebAPIBaseSerializer.class, getClass());
        this.jsonService = WebAPIAPI.getJsonService().orElse(null);
        this.permissionService = WebAPIAPI.getPermissionService().orElse(null);
    }
    private WebAPIBaseSerializer(Class<T> t) {
        super(t);
    }

    /**
     * Checks to see if details should be serialized.
     * @param provider The current provider.
     * @return True if details for the object should be serialized, false otherwise.
     */
    protected boolean shouldWriteDetails(SerializerProvider provider) {
        return ((AtomicBoolean)provider.getAttribute("details")).get();
    }

    /**
     * Serializes the specified {@link DataHolder} on the current object. This adds all known data as properties to
     * the current object.
     * @param provider The current provider.
     * @param holder The {@link DataHolder} to serialize.
     * @throws IOException Is thrown when serialization fails.
     */
    protected void writeData(SerializerProvider provider, DataHolder holder) throws IOException {
        for (Map.Entry<String, Class> entry : jsonService.getSupportedData().entrySet()) {
            Optional<?> m = holder.get(entry.getValue());

            if (!m.isPresent())
                continue;

            writeField(provider, entry.getKey(), m.get());
        }
    }

    /**
     * Writes a field for the current object with the specified value.
     * @param provider The current provider.
     * @param key The key of the entry.
     * @param value The value of the object.
     * @throws IOException Is thrown when serialization fails.
     */
    protected void writeField(SerializerProvider provider, String key, Object value) throws IOException {
        writeField(provider, key, value, Tristate.UNDEFINED);
    }

    /**
     * Writes a field for the current object with the specified value and determines if details for this object should
     * be serialized aswell.
     * @param provider The current provider.
     * @param key The key of the entry.
     * @param value The value of the object.
     * @param details Set to true to serialize the properties of the object, false to ignore them. Undefined uses
     *                the behaviour currently applicable to this object.
     * @throws IOException Is thrown when serialization fails.
     */
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

        if (!permissionService.permits(incs, parents)) {
            parents.remove(key);
            return;
        }

        provider.getGenerator().writeObjectField(key, value);

        currDetails.set(prevDetails);

        parents.remove(key);
    }

    /**
     * Writes the specified value as the current object.
     * @param provider The current provider.
     * @param value The value that is written.
     * @throws IOException Is thrown when serialization fails.
     */
    protected void writeValue(SerializerProvider provider, Object value) throws IOException {
        writeValue(provider, value, Tristate.UNDEFINED);
    }

    /**
     * Writes the specified value as the current object, choosing weather to include details or not.
     * @param provider The current provider.
     * @param value the value that is written.
     * @param details Set to true to serialize the properties of the object, false to ignore them. Undefined uses
     *                the behaviour currently applicable to this object.
     * @throws IOException Is thrown when serialization fails.
     */
    protected void writeValue(SerializerProvider provider, Object value, Tristate details) throws IOException {
        TreeNode<String, Boolean> incs = (TreeNode<String, Boolean>)provider.getAttribute("includes");
        List<String> parents = (List<String>)provider.getAttribute("parents");

        AtomicBoolean currDetails = (AtomicBoolean)provider.getAttribute("details");
        boolean prevDetails = currDetails.get();
        if (details == Tristate.TRUE) {
            currDetails.set(true);
        } else if (details == Tristate.FALSE) {
            currDetails.set(false);
        }

        if (!permissionService.permits(incs, parents)) {
            currDetails.set(prevDetails);
            return;
        }

        provider.getGenerator().writeObject(value);

        currDetails.set(prevDetails);
    }
}
