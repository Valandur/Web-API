package valandur.webapi.swagger;

import io.swagger.annotations.*;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContextImpl;
import io.swagger.converter.ModelConverters;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.ReaderListener;
import io.swagger.models.*;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import org.spongepowered.api.data.manipulator.DataManipulator;
import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.util.Constants;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@io.swagger.annotations.SwaggerDefinition(
        info = @Info(
                title = Constants.NAME,
                version = Constants.VERSION,
                description = "Access Sponge powered Minecraft servers through a WebAPI\n\n" +
                        "# Introduction\n" +
                        "This is the documentation of the various API routes offered by the WebAPI plugin.\n\n" +
                        "This documentation assumes that you are familiar with the basic concepts of Web API's, " +
                        "such as `GET`, `PUT`, `POST` and `DELETE` methods, request `HEADERS` and `RESPONSE CODES` " +
                        "and `JSON` data.\n\n" +
                        "By default this documentation can be found at http:/localhost:8080 " +
                        "(while your minecraft server is running) and the various routes start with " +
                        "http:/localhost:8080/api/v5...\n\n" +
                        "As a quick test try reaching the route http:/localhost:8080/api/v5/info " +
                        "(remember that you can only access \\\"localhost\\\" routes on the server on which you " +
                        "are running minecraft).\n" +
                        "This route should show you basic information about your server, like the motd and " +
                        "player count.\n\n" +
                        "# List endpoints\n" +
                        "Lots of objects offer an endpoint to list all objects (e.g. `GET: /world` to get all worlds). " +
                        "These endpoints return only the properties marked 'required' by default, because the list " +
                        "might be quite large. If you want to return ALL data for a list endpoint add the query " +
                        "parameter `details`, (e.g. `GET: /world?details`).\n\n" +
                        "> Remember that in this case the data returned by the endpoint might be quite large.\n\n" +
                        "# Debugging endpoints\n" +
                        "Apart from the `?details` flag you can also pass some other flags for debugging purposes. " +
                        "Remember that you must include the first query parameter with `?`, and further ones with `&`:\n\n" +
                        "`details`: Includes details for list endpoints\n\n" +
                        "`accept=[json/xml]`: Manually set the accept content type. This is good for browser testing, " +
                        "**BUT DON'T USE THIS IN PRODUCTION, YOU CAN SUPPLY THE `Accepts` HEADER FOR THAT**\n\n" +
                        "`pretty`: Pretty prints the data, also good for debugging in the browser.\n\n" +
                        "An example request might look like this: " +
                        "`http://localhost:8080/api/v5/world?details&accpet=json&pretty&key=MY-API-KEY`\n\n" +
                        "# Additional data\n" +
                        "Certain endpoints (such as `/player`, `/entity` and `/tile-entity` have additional " +
                        "properties which are not documented here, because the data depends on the concrete " +
                        "object type (eg. `Sheep` have a wool color, others do not) and on the other plugins/mods " +
                        "that are running on your server which might add additional data.\n\n" +
                        "You can also find more information in the github docs " +
                        "(https:/github.com/Valandur/Web-API/tree/master/docs/DATA.md)",
                contact = @Contact(
                        name = "Valandur",
                        email = "inithilian@gmail.com",
                        url = "https://github.com/Valandur"
                ),
                license = @License(
                        name = "MIT",
                        url = "https://github.com/Valandur/Web-API/blob/master/LICENSE"
                )
        ),
        consumes = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML },
        produces = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML },
        securityDefinition = @SecurityDefinition(apiKeyAuthDefinitions = {
                @ApiKeyAuthDefinition(
                        key = "ApiKeyHeader",
                        name = "X-WebAPI-Key",
                        description = "Authorize using an HTTP header. This can also be done using the " +
                                "`Authorization` header with a `Bearer` token",
                        in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER),
                @ApiKeyAuthDefinition(
                        key = "ApiKeyQuery",
                        name = "key",
                        description = "Authorize using a query value.",
                        in = ApiKeyAuthDefinition.ApiKeyLocation.QUERY),
        })
)
public class SwaggerDefinition implements ReaderListener {

    private static QueryParameter constructQueryParameter(String name, String description, String... values) {
        QueryParameter param = new QueryParameter();
        param.setName(name);
        param.setDescription(description);
        if (values != null && values.length > 0) {
            param.setType("string");
            param.setEnum(Arrays.asList(values));
        } else {
            param.setType("boolean");
        }
        return param;
    }
    private static Response constructResponse(int status, String error) {
        Property statusProp = new IntegerProperty()
                .description("The status code of the error (also provided in the HTTP header)");
        statusProp.setExample(status);

        Property errorProp = new StringProperty()
                .description("The error message describing the error");
        errorProp.setExample((Object)error);

        return new Response()
                .description(error)
                .schema(new ObjectProperty()
                        .property("status", statusProp)
                        .property("error", errorProp));
    }

    @Override
    public void beforeScan(Reader reader, Swagger swagger) {
        swagger.addParameter("details", constructQueryParameter(
                "details",
                "Add to include additional details, omit or false otherwise"));
        swagger.addParameter("accept", constructQueryParameter(
                "accept",
                "Override the 'Accept' request header (useful for debugging your requests)",
                "json", "xml"));
        swagger.addParameter("pretty", constructQueryParameter(
                "pretty",
                "Add to make the Web-API pretty print the response (useful for debugging your requests)"));

        swagger.response("400", constructResponse(400, "Bad request"));
        swagger.response("401", constructResponse(401, "Unauthorized"));
        swagger.response("403", constructResponse(403, "Access denied"));
        swagger.response("404", constructResponse(404, "Not found"));
        swagger.response("500", constructResponse(500, "Internal server error"));
        swagger.response("501", constructResponse(501, "Not implemented"));
    }

    @Override
    public void afterScan(Reader reader, Swagger swagger) {
        List<String> webapiTags = new ArrayList<>();
        List<String> integrationTags = new ArrayList<>();

        swagger.setTags(new ArrayList<>());

        // Collect tags for servlets
        for (Class<? extends BaseServlet> servletClass : WebAPI.getServletService().getRegisteredServlets().values()) {
            Api api = servletClass.getAnnotation(Api.class);
            String descr = api.value();

            Set<String> tags = new HashSet<>(Arrays.asList(api.tags()));
            tags.addAll(Arrays.stream(servletClass.getMethods())
                    .flatMap(m -> Arrays.stream(m.getAnnotationsByType(ApiOperation.class)))
                    .flatMap(a -> Arrays.stream(a.tags()))
                    .filter(t -> !t.isEmpty())
                    .collect(Collectors.toList()));

            if (servletClass.getPackage().getName().startsWith("valandur.webapi.servlet")) {
                for (String tag : tags) {
                    webapiTags.add(tag);
                    swagger.addTag(new io.swagger.models.Tag().name(tag).description(descr));
                }
            } else {
                for (String tag : tags) {
                    integrationTags.add(tag);
                    swagger.addTag(new io.swagger.models.Tag().name(tag).description(descr));
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

        // Dirty hack to set up our model converter, because we need access to the context,
        // otherwise we have to do multiple calls resolving the model, which isn't worth it
        List<ModelConverter> converters = new ArrayList<>();
        try {
            Field f = ModelConverters.class.getDeclaredField("converters");
            f.setAccessible(true);
            converters = (List<ModelConverter>) f.get(ModelConverters.getInstance());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            WebAPI.sentryCapture(e);
        }

        Set<String> oldModels = swagger.getDefinitions().keySet();

        // Generate types for additional data
        Map<String, Property> props = new LinkedHashMap<>();
        // Collect all additional data from our serializer
        List<Map.Entry<String, Class<? extends DataManipulator<?, ?>>>> dataList =
                WebAPI.getSerializeService().getSupportedData().entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .collect(Collectors.toList());
        // Iterate all the additional data
        for (Map.Entry<String, Class<? extends DataManipulator<?, ?>>> entry : dataList) {
            String key = entry.getKey();

            // Create our context and resolve the model (manually, instead of ModelConverters.getInstance().readYYY()
            ModelConverterContextImpl context = new ModelConverterContextImpl(converters);
            Property prop = context.resolveProperty(entry.getValue(), null);

            // Read the view as a model, and add all the read models to the definition
            for (Map.Entry<String, Model> modelEntry : context.getDefinedModels().entrySet()) {
                if (oldModels.contains(modelEntry.getKey())) {
                    continue;
                }

                swagger.addDefinition(modelEntry.getKey(), modelEntry.getValue());
            }

            // Add the data we read
            props.put(key, prop);
        }

        // Collect all additional properties from our serializer
        List<Map.Entry<Class<? extends org.spongepowered.api.data.Property<?, ?>>, String>> propList =
                WebAPI.getSerializeService().getSupportedProperties().entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .collect(Collectors.toList());
        // Iterate all the additional props
        for (Map.Entry<Class<? extends org.spongepowered.api.data.Property<?, ?>>, String> entry : propList) {
            String key = entry.getValue();

            // Create our context and resolve the model (manually, instead of ModelConverters.getInstance().readYYY()
            ModelConverterContextImpl context = new ModelConverterContextImpl(converters);
            Property prop = context.resolveProperty(entry.getKey(), null);

            // Read the view as a model, and add all the read models to the definition
            for (Map.Entry<String, Model> modelEntry : context.getDefinedModels().entrySet()) {
                if (oldModels.contains(modelEntry.getKey())) {
                    continue;
                }

                swagger.addDefinition(modelEntry.getKey(), modelEntry.getValue());
            }

            // Add the property we read to the data props
            props.put(key, prop);
        }

        // Add the additional properties to the required DataObjects
        // TODO: Automate this with an annotation
        Map<String, Model> defs = swagger.getDefinitions();
        attachAdditionalProps(defs.get("PlayerFull"), props);
        attachAdditionalProps(defs.get("WorldFull"), props);
        attachAdditionalProps(defs.get("Entity"), props);
        attachAdditionalProps(defs.get("TileEntity"), props);
        attachAdditionalProps(defs.get("ItemStack"), props);
        attachAdditionalProps(defs.get("FluidStack"), props);

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
    }

    private void attachAdditionalProps(Model model, Map<String, Property> dataProps) {
        if (model instanceof ComposedModel) {
            model = ((ComposedModel) model).getChild();
        }
        Map<String, Property> props = model.getProperties();
        if (props == null) {
            props = new HashMap<>();
        }
        for (Map.Entry<String, Property> entry : dataProps.entrySet()) {
            props.put(entry.getKey(), entry.getValue());
        }
        model.setProperties(props);
    }


    public static class TagGroup {
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
