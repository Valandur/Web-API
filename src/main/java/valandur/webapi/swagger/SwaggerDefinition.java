package valandur.webapi.swagger;

import io.swagger.annotations.*;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.ReaderListener;
import io.swagger.models.Swagger;
import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.util.Constants;

import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.stream.Collectors;

@io.swagger.annotations.SwaggerDefinition(
        info = @Info(
                title = Constants.NAME,
                version = Constants.VERSION,
                description = "Access Sponge powered Minecraft servers through a WebAPI\n\n# Introduction\nThis is the documentation of the various API routes offered by the WebAPI plugin.\n\nThis documentation assumes that you are familiar with the basic concepts of Web API's, such as `GET`, `PUT`, `POST` and `DELETE` methods,\nrequest `HEADERS` and `RESPONSE CODES` and `JSON` data.\n\nBy default this documentation can be found at http:/localhost:8080 (while your minecraft server is running) and the various routes start with http:/localhost:8080/api/...\n\nAs a quick test try reaching the route http:/localhost:8080/api/info (remember that you can only access \\\"localhost\\\" routes on the server on which you are running minecraft).\nThis route should show you basic information about your server, like the motd and player count.\n\n# Additional data\nCertain endpoints (such as `/player`, `/entity` and `/tile-entity` have additional properties which are not documented here, because the data depends on the concrete\nobject type (eg. `Sheep` have a wool color, others do not) and on the other plugins/mods that are running on your server which might add additional data.\n\nYou can also find more information in the github docs (https:/github.com/Valandur/Web-API/tree/master/docs/DATA.md)",
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
        schemes = { io.swagger.annotations.SwaggerDefinition.Scheme.HTTP, io.swagger.annotations.SwaggerDefinition.Scheme.HTTPS },
        securityDefinition = @SecurityDefinition(apiKeyAuthDefinitions = {
                @ApiKeyAuthDefinition(key = "ApiKeyHeader", name = "X-WebAPI-Key", in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER),
                @ApiKeyAuthDefinition(key = "ApiKeyQuery", name = "key", in = ApiKeyAuthDefinition.ApiKeyLocation.QUERY)
        })
)
public class SwaggerDefinition implements ReaderListener {

    @Override
    public void beforeScan(Reader reader, Swagger swagger) {

    }

    @Override
    public void afterScan(Reader reader, Swagger swagger) {
        Map<String, String> webapiTags = new HashMap<>();
        Map<String, String> integrationTags = new HashMap<>();

        swagger.setTags(new ArrayList<>());

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
                    webapiTags.put(tag, descr);
                    swagger.addTag(new io.swagger.models.Tag().name(tag).description(descr));
                }
            } else {
                for (String tag : tags) {
                    integrationTags.put(tag, descr);
                    swagger.addTag(new io.swagger.models.Tag().name(tag).description(descr));
                }
            }
        }

        // Add tag groups for redoc
        swagger.vendorExtension("x-tagGroups", Arrays.asList(
                new TagGroup("Web-API", new ArrayList<>(webapiTags.keySet())),
                new TagGroup("Integrations", new ArrayList<>(integrationTags.keySet()))
        ));
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
