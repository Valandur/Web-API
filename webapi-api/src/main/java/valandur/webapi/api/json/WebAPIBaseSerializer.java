package valandur.webapi.api.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.jodah.typetools.TypeResolver;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.property.PropertyHolder;
import org.spongepowered.api.util.Tristate;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.cache.ICacheService;
import valandur.webapi.api.permission.IPermissionService;
import valandur.webapi.api.util.TreeNode;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The base serializer that other serializers must inherit from.
 * This serializer exposes the Web-API services as class variables for convenience.
 * @param <T>
 */
public abstract class WebAPIBaseSerializer<T> extends StdSerializer<T> {

    protected Map<Long, SerializerProvider> currentProviders = new ConcurrentHashMap<>();
    protected Map<Long, Stack<Boolean>> localObjects = new ConcurrentHashMap<>();
    protected Map<Long, Stack<Boolean>> localArrays = new ConcurrentHashMap<>();

    protected ICacheService cacheService;
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
        this.cacheService = WebAPIAPI.getCacheService().orElse(null);
        this.jsonService = WebAPIAPI.getJsonService().orElse(null);
        this.permissionService = WebAPIAPI.getPermissionService().orElse(null);
    }
    private WebAPIBaseSerializer(Class<T> t) {
        super(t);
    }

    @Override
    public final void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        long id = Thread.currentThread().getId();

        currentProviders.put(id, provider);
        localObjects.put(id, new Stack<>());
        localArrays.put(id, new Stack<>());
        serialize(value);
        currentProviders.remove(id);
        localObjects.remove(id);
        localArrays.remove(id);
    }

    protected abstract void serialize(T value) throws IOException;


    private Stack<Boolean> getLocalObjects() {
        return localObjects.get(Thread.currentThread().getId());
    }
    private Stack<Boolean> getLocalArrays() {
        return localArrays.get(Thread.currentThread().getId());
    }
    private SerializerProvider getCurrentProvider() {
        return currentProviders.get(Thread.currentThread().getId());
    }

    /**
     * Writes the start for a new object.
     * @throws IOException Is thrown when serialization fails.
     */
    protected void writeStartObject() throws IOException {
        getLocalObjects().push(false);
        getCurrentProvider().getGenerator().writeStartObject();
    }

    /**
     * Write the object start for a new inline object with the specified field name.
     * @param fieldName The field name under which the object will be created.
     * @return True if the object data should be written, false otherwise.
     * @throws IOException Is thrown when serialization fails.
     */
    protected boolean writeObjectFieldStart(String fieldName) throws IOException {
        SerializerProvider provider = getCurrentProvider();

        TreeNode<String, Boolean> incs = (TreeNode<String, Boolean>)provider.getAttribute("includes");
        List<String> parents = (List<String>)provider.getAttribute("parents");

        parents.add(fieldName);

        if (!permissionService.permits(incs, parents)) {
            parents.remove(fieldName);
            return false;
        }

        getLocalObjects().push(true);

        provider.getGenerator().writeObjectFieldStart(fieldName);
        return true;
    }

    /**
     * Writes the end for an object.
     * @throws IOException Is thrown when serialization fails.
     */
    protected void writeEndObject() throws IOException {
        SerializerProvider provider = getCurrentProvider();

        if (getLocalObjects().pop()) {
            List<String> parents = (List<String>) provider.getAttribute("parents");
            parents.remove(parents.size() - 1);
        }

        provider.getGenerator().writeEndObject();
    }

    /**
     * Writes the start for a new array.
     * @throws IOException Is thrown when serialization fails.
     */
    protected void writeStartArray() throws IOException {
        getLocalArrays().push(false);
        getCurrentProvider().getGenerator().writeStartArray();
    }

    /**
     * Writes the start for a new inline array with the specified field name.
     * @param fieldName the field name under which the object will be created.
     * @return True if the object data should be written, false otherwise.
     * @throws IOException Is thrown when serialization fails.
     */
    protected boolean writeArrayFieldStart(String fieldName) throws IOException {
        SerializerProvider provider = getCurrentProvider();

        TreeNode<String, Boolean> incs = (TreeNode<String, Boolean>)provider.getAttribute("includes");
        List<String> parents = (List<String>)provider.getAttribute("parents");

        parents.add(fieldName);

        if (!permissionService.permits(incs, parents)) {
            parents.remove(fieldName);
            return false;
        }

        getLocalArrays().push(true);

        provider.getGenerator().writeArrayFieldStart(fieldName);
        return true;
    }

    /**
     * Writes the end for an array.
     * @throws IOException Is thrown when serialization fails.
     */
    protected void writeEndArray() throws IOException {
        SerializerProvider provider = getCurrentProvider();
        long id = Thread.currentThread().getId();

        if (getLocalArrays().pop()) {
            List<String> parents = (List<String>) provider.getAttribute("parents");
            parents.remove(parents.size() - 1);
        }

        provider.getGenerator().writeEndArray();
    }

    /**
     * Checks to see if details should be serialized.
     * @return True if details for the object should be serialized, false otherwise.
     */
    protected boolean shouldWriteDetails() {
        SerializerProvider provider = getCurrentProvider();
        return ((AtomicBoolean)provider.getAttribute("details")).get();
    }

    /**
     * Serializes the specified data on the current object. This adds all known data as properties to
     * the current object.
     * @param holder The data holder holding the data to serialize
     * @throws IOException Is thrown when serialization fails.
     */
    protected void writeData(DataHolder holder) throws IOException {
        for (Map.Entry<String, Class<? extends DataManipulator>> entry :
                jsonService.getSupportedData().entrySet()) {
            Optional<?> m = holder.get(entry.getValue());

            if (!m.isPresent())
                continue;

            writeField(entry.getKey(), m.get());
        }
    }

    /**
     * Serializes the specified data map on the current object.
     * @param data The data map to serialize.
     * @throws IOException Is thrown when serialization fails.
     */
    protected void writeData(Map<String, Object> data) throws IOException {
        if (data == null) return;

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            writeField(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Writes all properties to the current object.
     * @param holder The holder of the properties.
     * @throws IOException Is thrown when serialization fails.
     */
    protected void writeProperties(PropertyHolder holder) throws IOException {
        for (Property<?, ?> property : holder.getApplicableProperties()) {
            String key = property.getKey().toString();
            key = key.replace("Property", "");
            key = Character.toLowerCase(key.charAt(0)) + key.substring(1);
            writeField(key, property.getValue());
        }
    }

    /**
     * Writes a field for the current object with the specified value.
     * @param key The key of the entry.
     * @param value The value of the object.
     * @throws IOException Is thrown when serialization fails.
     */
    protected void writeField(String key, Object value) throws IOException {
        SerializerProvider provider = getCurrentProvider();

        writeField(key, value, Tristate.UNDEFINED);
    }

    /**
     * Writes a field for the current object with the specified value and determines if details for this object should
     * be serialized aswell.
     * @param key The key of the entry.
     * @param value The value of the object.
     * @param details Set to true to serialize the properties of the object, false to ignore them. Undefined uses
     *                the behaviour currently applicable to this object.
     * @throws IOException Is thrown when serialization fails.
     */
    protected void writeField(String key, Object value, Tristate details) throws IOException {
        SerializerProvider provider = getCurrentProvider();

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
     * @param value The value that is written.
     * @throws IOException Is thrown when serialization fails.
     */
    protected void writeValue(Object value) throws IOException {
        SerializerProvider provider = getCurrentProvider();
        writeValue(value, Tristate.UNDEFINED);
    }

    /**
     * Writes the specified value as the current object, choosing weather to include details or not.
     * @param value the value that is written.
     * @param details Set to true to serialize the properties of the object, false to ignore them. Undefined uses
     *                the behaviour currently applicable to this object.
     * @throws IOException Is thrown when serialization fails.
     */
    protected void writeValue(Object value, Tristate details) throws IOException {
        SerializerProvider provider = getCurrentProvider();

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
