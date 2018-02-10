package valandur.webapi.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.jaxrs.cfg.JaxRSFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import valandur.webapi.WebAPI;
import valandur.webapi.api.permission.IPermissionService;
import valandur.webapi.api.util.TreeNode;
import valandur.webapi.security.SecurityContext;
import valandur.webapi.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.util.Map;

@Provider
@Consumes(MediaType.WILDCARD)
@Produces(MediaType.WILDCARD)
public class SerializationProvider extends JacksonJsonProvider {

    @Context
    HttpServletRequest request;

    public SerializationProvider() {
        disable(JaxRSFeature.CACHE_ENDPOINT_WRITERS);
    }

    @Override
    protected boolean hasMatchingMediaType(MediaType mediaType) {
        return MediaType.APPLICATION_XML_TYPE.isCompatible(mediaType) || super.hasMatchingMediaType(mediaType);
    }

    @Override
    public ObjectMapper locateMapper(Class<?> type, MediaType mediaType) {
        Map<String, String> queryParams = Util.getQueryParams(request);
        boolean xml = MediaType.APPLICATION_XML_TYPE.isCompatible(mediaType);
        // Allow override the media type with a header (for better browser debugging)
        if (queryParams.containsKey("accept")) {
            String acc = queryParams.get("accept");
            if (acc.equalsIgnoreCase("json"))
                xml = false;
            if (acc.equalsIgnoreCase("xml"))
                xml = true;
        }

        // If we're serializing an error return a normal XML/Object mapper, just in case the error
        // happened while creating the mapper, so that we don't get an infinite recursion
        if (Throwable.class.isAssignableFrom(type)) {
            return xml ? new XmlMapper() : new ObjectMapper();
        }

        SecurityContext ctx = (SecurityContext)request.getAttribute("security");
        TreeNode<String, Boolean> perms = IPermissionService.permitAllNode();
        if (ctx != null && ctx.getEndpointPerms() != null) {
            perms = ctx.getEndpointPerms();
        }

        Boolean det = (Boolean)request.getAttribute("details");
        boolean details = (det != null && det) || queryParams.containsKey("details");

        SerializeService srv = WebAPI.getSerializeService();
        ObjectMapper mapper = srv.getDefaultObjectMapper(xml, details, perms);
        if (queryParams.containsKey("pretty"))
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }
}
