package valandur.webapi.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.jaxrs.cfg.JaxRSFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JsonEndpointConfig;
import org.eclipse.jetty.io.EofException;
import valandur.webapi.WebAPI;
import valandur.webapi.security.PermissionService;
import valandur.webapi.security.SecurityContext;
import valandur.webapi.util.TreeNode;
import valandur.webapi.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

@Provider
@Consumes(MediaType.WILDCARD)
@Produces(MediaType.WILDCARD)
public class SerializationProvider extends JacksonJsonProvider {

    @Context
    HttpServletRequest request;

    @Context
    HttpServletResponse response;

    public SerializationProvider() {
        disable(JaxRSFeature.CACHE_ENDPOINT_WRITERS);
        disable(JaxRSFeature.CACHE_ENDPOINT_READERS);
    }

    @Override
    protected boolean hasMatchingMediaType(MediaType mediaType) {
        return MediaType.TEXT_PLAIN_TYPE.isCompatible(mediaType) ||
                MediaType.TEXT_HTML_TYPE.isCompatible(mediaType) ||
                MediaType.APPLICATION_XML_TYPE.isCompatible(mediaType) ||
                super.hasMatchingMediaType(mediaType);
    }

    @Override
    protected void _modifyHeaders(Object value, Class<?> type, Type genericType, Annotation[] annotations,
                                  MultivaluedMap<String, Object> httpHeaders, JsonEndpointConfig endpoint)
            throws IOException {
        super._modifyHeaders(value, type, genericType, annotations, httpHeaders, endpoint);

        // If the mapper was changed with a query parameter, we have to update
        // the content type header to reflect that
        Map<String, String> queryParams = Util.getQueryParams(request);
        if (queryParams.containsKey("accept")) {
            String acc = queryParams.get("accept");
            if (acc.equalsIgnoreCase("json")) {
                httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            }
            if (acc.equalsIgnoreCase("xml")) {
                httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML);
            }
        }
    }

    @Override
    public ObjectMapper locateMapper(Class<?> type, MediaType mediaType) {
        Map<String, String> queryParams = Util.getQueryParams(request);
        boolean xml = MediaType.APPLICATION_XML_TYPE.isCompatible(mediaType);
        // Allow override the media type with a header (for better browser debugging)
        if (queryParams.containsKey("accept")) {
            String acc = queryParams.get("accept");
            if (acc.equalsIgnoreCase("json")) xml = false;
            if (acc.equalsIgnoreCase("xml")) xml = true;
        }

        // If we're serializing an error return a normal XML/Object mapper, just in case the error
        // happened while creating the mapper, so that we don't get an infinite recursion
        if (Throwable.class.isAssignableFrom(type)) {
            return xml ? new XmlMapper() : new ObjectMapper();
        }

        SecurityContext ctx = (SecurityContext)request.getAttribute("security");
        TreeNode perms = PermissionService.permitAllNode();
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

    @Override
    public void writeTo(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) {
        try {
            super.writeTo(value, type, genericType, annotations, mediaType, httpHeaders, entityStream);
        } catch (IOException e) {
            if (e instanceof EofException) return;
            e.printStackTrace();
        }
    }
}
