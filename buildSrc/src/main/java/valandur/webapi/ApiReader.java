package valandur.webapi;

import com.benjaminsproule.swagger.gradleplugin.classpath.ClassFinder;
import com.benjaminsproule.swagger.gradleplugin.model.ApiSourceExtension;
import com.benjaminsproule.swagger.gradleplugin.reader.JaxrsReader;
import io.swagger.jaxrs.ext.SwaggerExtension;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.properties.Property;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class ApiReader extends JaxrsReader {

    public ApiReader(ApiSourceExtension apiSourceExtension, Set<Type> typesToSkip,
                     List<SwaggerExtension> swaggerExtensions, ClassFinder classFinder) {
        super(apiSourceExtension, typesToSkip, swaggerExtensions, classFinder);
    }

    @Override
    public Swagger read() {
        Swagger swagger = new Swagger();

        List<String> webapiTags = new ArrayList<>();
        List<String> integrationTags = new ArrayList<>();

        for (Path path : swagger.getPaths().values()) {
            for (Operation op : path.getOperations()) {
                if (op.getTags().contains("Integration")) {
                    for (String tag : op.getTags()) {
                        webapiTags.add(tag);
                        swagger.addTag(new io.swagger.models.Tag().name(tag));
                    }
                } else {
                    for (String tag : op.getTags()) {
                        integrationTags.add(tag);
                        swagger.addTag(new io.swagger.models.Tag().name(tag));
                    }
                }
            }
        }

        // Sort properties by "required" and alphabetically
        for (Model model : swagger.getDefinitions().values()) {
            Map<String, Property> props = new LinkedHashMap<>();
            if (model.getProperties() == null) continue;

            List<Map.Entry<String, Property>> newProps = model.getProperties().entrySet().stream()
                    .sorted((p1, p2) -> {
                        int req = Boolean.compare(p2.getValue().getRequired(), p1.getValue().getRequired());
                        if (req != 0) return req;
                        return p1.getKey().compareTo(p2.getKey());
                    }).collect(Collectors.toList());
            for (Map.Entry<String, Property> newProp : newProps) {
                props.put(newProp.getKey(), newProp.getValue());
            }
            model.getProperties().clear();
            model.setProperties(props);
        }

        // Sort tags alphabetically
        webapiTags.sort(String::compareTo);
        integrationTags.sort(String::compareTo);

        // Sort paths alphabetically
        List<Map.Entry<String, Path>> paths = swagger.getPaths().entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .collect(Collectors.toList());
        swagger.setPaths(new LinkedHashMap<>());
        for (Map.Entry<String, Path> entry : paths) {
            swagger.path(entry.getKey(), entry.getValue());
        }

        // Add tag groups for redoc
        swagger.vendorExtension("x-tagGroups", Arrays.asList(
                new TagGroup("Web-API", webapiTags),
                new TagGroup("Integrations", integrationTags)
        ));

        return swagger;
    }

    static class TagGroup {
        private String name;
        public String getName() {
            return name;
        }

        private List<String> tags;
        public List<String> getTags() {
            return tags;
        }

        public TagGroup(String name, List<String> tags) {
            this.name = name;
            this.tags = tags;
        }
    }
}
