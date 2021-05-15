package io.valandur.webapi.security;

import io.valandur.webapi.WebAPIBase;
import io.valandur.webapi.logger.Logger;
import jakarta.annotation.Priority;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.lang.reflect.Method;

import static io.valandur.webapi.security.SecurityService.*;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private final SecurityService srv;
    private final Logger logger;

    @Context
    private ResourceInfo info;

    @Context
    private HttpServletRequest request;

    public SecurityFilter() {
        var webapi = WebAPIBase.getInstance();
        this.srv = webapi.getSecurityService();
        this.logger = webapi.getLogger();
    }

    @Override
    public void filter(ContainerRequestContext reqContext) throws IOException {
        // Exit early on options requests
        if (HttpMethod.OPTIONS.equalsIgnoreCase(reqContext.getMethod())) {
            reqContext.abortWith(Response.ok().build());
            return;
        }

        String address = getRealAddress(request.getRemoteAddr(), reqContext.getHeaderString(X_FORWARDED_FOR));

        String key = reqContext.getHeaderString(API_KEY);
        if (key == null || key.isEmpty()) {
            key = reqContext.getUriInfo().getQueryParameters().getFirst("key");
        }
        if (key == null || key.isEmpty()) {
            key = reqContext.getHeaderString(HttpHeaders.AUTHORIZATION);
            if (key != null) key = key.substring(key.indexOf(" ") + 1);
        }

        if (!srv.whitelistContains(address)) {
            logger.warn(address + " | Not on whitelist");
            throw new NotAuthorizedException("Access denied");
        }

        if (srv.blacklistContains(address)) {
            logger.warn(address + " | On blacklist");
            throw new NotAuthorizedException("Access denied");
        }

        if (key == null || key.isEmpty()) {
            logger.warn(address + " | No key");
            throw new NotAuthorizedException("Access denied");
        }

        var perms = srv.getPerms(key);
        if (perms == null) {
            logger.warn(address + " | Invalid key: " + key);
            throw new ForbiddenException("Access denied");
        }

        if (srv.isRateLimited(key, perms)) {
            logger.warn(address + " | Rate limited: " + key);
            throw new WebApplicationException("Rate limited",
                    Response.status(Response.Status.TOO_MANY_REQUESTS).build());
        }

        Method method = info.getResourceMethod();
        AccessControl[] accessControls = method.getAnnotationsByType(AccessControl.class);
        for (AccessControl control : accessControls) {
            if (control.value() == Access.WRITE && perms.access == Access.READ) {
                logger.warn(address + " | No access: " + key);
                throw new ForbiddenException("Access denied");
            }
        }
    }

    @Override
    public void filter(ContainerRequestContext reqContext, ContainerResponseContext resContext) throws IOException {
        var headers = resContext.getHeaders();
        headers.add(ACCESS_CONTROL_ALLOW_ORIGIN, ACCESS_CONTROL_ORIGIN);
        headers.add(ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_METHODS);
        headers.add(ACCESS_CONTROL_ALLOW_HEADERS, ACCESS_CONTROL_HEADERS);
    }

    private String getRealAddress(String address, String forwardedFor) {
        if (forwardedFor == null)
            return address;

        // First check the actual IP that we got. If that is not a trusted proxy we're done.
        if (!srv.containsProxyIP(address)) {
            logger.warn(address + " sent " + X_FORWARDED_FOR + " header, but is not a proxy. Header will be ignored!");
            return address;
        }

        String[] ips = forwardedFor.split(",");

        // Traverse the X-Forwarded-For header backwards and take the first IP that we don't trust.
        for (int i = ips.length - 1; i >= 0; i--) {
            String ip = ips[i].trim();
            if (srv.containsProxyIP(ip)) {
                continue;
            }

            if (i > 0) {
                logger.warn(ips[i].trim() + " sent " + X_FORWARDED_FOR +
                        " header, but is not a proxy. Header will be ignored!");
            }
            return ips[i];
        }

        // We usually shouldn't get here, but if we don't it means we trusted all proxy ips, so just
        // return the last one of those.
        return ips[ips.length - 1];
    }
}
