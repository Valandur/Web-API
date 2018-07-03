package valandur.webapi.security;

import com.google.common.net.HttpHeaders;
import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.WebAPI;
import valandur.webapi.servlet.base.ExplicitDetails;
import valandur.webapi.servlet.base.Permission;
import valandur.webapi.util.TreeNode;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static valandur.webapi.security.SecurityService.*;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

    private SecurityService srv;

    private Map<String, Double> lastCall = new ConcurrentHashMap<>();
    private AtomicLong calls = new AtomicLong(0);

    @Context
    private ResourceInfo resourceInfo;
    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;


    public SecurityFilter() {
        this.srv = WebAPI.getSecurityService();
    }

    @Override
    public void filter(ContainerRequestContext context) {
        String addr = getRealAddr(request);
        String target = context.getUriInfo().getPath();

        request.setAttribute("ip", addr);

        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ACCESS_CONTROL_ORIGIN);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_METHODS);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, ACCESS_CONTROL_HEADERS);

        // Exit early on options requests
        if (HttpMethod.OPTIONS.asString().equalsIgnoreCase(context.getMethod())) {
            context.abortWith(Response.ok().build());
            return;
        }

        if (!srv.whitelistContains(addr)) {
            WebAPI.getLogger().warn(addr + " is not on whitelist: " + target);
            throw new ForbiddenException();
        } else if (srv.blacklistContains(addr)) {
            WebAPI.getLogger().warn(addr + " is on blacklist: " + target);
            throw new ForbiddenException();
        }

        String key = context.getHeaderString(API_KEY_HEADER);
        if (key == null || key.isEmpty()) {
            key = context.getUriInfo().getQueryParameters().getFirst("key");
        }
        if (key == null || key.isEmpty()) {
            key = context.getHeaderString(HttpHeaders.AUTHORIZATION);
            if (key != null) key = key.substring(key.indexOf(" ") + 1);
        }

        PermissionStruct permStruct;
        if (key != null) {
            permStruct = srv.getPermissions(key);
        } else {
            key = DEFAULT_KEY;
            permStruct = srv.getDefaultPermissions();
        }

        // Add new security context
        SecurityContext securityContext = new SecurityContext(permStruct);
        context.setSecurityContext(securityContext);
        request.setAttribute("security", securityContext);

        // Do rate limiting
        calls.incrementAndGet();

        if (permStruct.getRateLimit() > 0) {
            double time = System.nanoTime() / 1000000000d;

            if (lastCall.containsKey(key) && time - lastCall.get(key) < 1d / permStruct.getRateLimit()) {
                WebAPI.getLogger().warn(addr + " has exceeded the rate limit when requesting " +
                        request.getRequestURI());
                throw new ClientErrorException("Rate limit exceeded", Response.Status.TOO_MANY_REQUESTS);
            }

            lastCall.put(key, time);
        }

        boolean details = true;
        Method method = resourceInfo.getResourceMethod();
        if (method.isAnnotationPresent(ExplicitDetails.class)) {
            ExplicitDetails dets = method.getAnnotation(ExplicitDetails.class);
            if (dets.value()) {
                details = false;
            }
        }
        request.setAttribute("details", details);

        String basePath = resourceInfo.getResourceClass().getAnnotation(Path.class).value();
        TreeNode perms = permStruct.getPermissions();

        Permission[] reqPerms = method.getAnnotationsByType(Permission.class);
        if (reqPerms.length == 0) {
            return;
        }

        // Calculate the sub-perms that apply for our endpoint
        for (Permission reqPerm : reqPerms) {
            if (!reqPerm.autoCheck()) {
                continue;
            }

            List<String> reqPermList = new ArrayList<>(Arrays.asList(reqPerm.value()));
            reqPermList.add(0, basePath);

            TreeNode methodPerms = SecurityService.subPermissions(perms, reqPermList);
            if (!methodPerms.getValue()) {
                WebAPI.getLogger().warn(addr + " does not have permisson to access " + target);
                if (key.equalsIgnoreCase(DEFAULT_KEY)) {
                    throw new NotAuthorizedException("Bearer realm=\"Web-API Access\"");
                } else {
                    throw new ForbiddenException();
                }
            }

            // Set the endpoint permissions to the first permissions listed
            if (securityContext.getEndpointPerms() == null)
                securityContext.setEndpointPerms(methodPerms);
        }
    }

    private String getRealAddr(HttpServletRequest request) {
        final String addr = request.getRemoteAddr();
        String forwardedFor = request.getHeader(HttpHeaders.X_FORWARDED_FOR);
        if (forwardedFor == null)
            return addr;

        // First check the actual IP that we got. If that is not a trusted proxy we're done.
        if (!srv.containsProxyIP(addr)) {
            WebAPI.getLogger().warn(addr + " sent " + HttpHeaders.X_FORWARDED_FOR +
                    " header, but is not a proxy. Header will be ignored!");
            return addr;
        }

        String[] ips = forwardedFor.split(",");

        // Traverse the X-Forwarded-For header backwards and take the first IP that we don't trust.
        for (int i = ips.length - 1; i >= 0; i--) {
            String ip = ips[i].trim();
            if (srv.containsProxyIP(ip)) {
                continue;
            }

            if (i > 0) {
                WebAPI.getLogger().warn(ips[i].trim() + " sent " + HttpHeaders.X_FORWARDED_FOR +
                        " header, but is not a proxy. Header will be ignored!");
            }
            return ips[i];
        }

        // We usually shouldn't get here, but if we don't it means we trusted all proxy ips, so just
        // return the last one of those.
        return ips[ips.length - 1];
    }
}
