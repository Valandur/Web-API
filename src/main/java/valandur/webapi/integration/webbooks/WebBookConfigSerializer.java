package valandur.webapi.integration.webbooks;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.List;

public class WebBookConfigSerializer implements TypeSerializer<WebBook> {

    @Override
    public WebBook deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        String id = value.getNode("id").getString();
        String title = value.getNode("title").getString();
        List<String> lines = value.getNode("lines").getList(TypeToken.of(String.class));
        return new WebBook(id, title, lines);
    }

    @Override
    public void serialize(TypeToken<?> type, WebBook obj, ConfigurationNode value) throws ObjectMappingException {
        value.getNode("id").setValue(obj.getId());
        value.getNode("title").setValue(obj.getTitle());
        value.getNode("lines").setValue(obj.getLines());
    }
}
