package io.valandur.webapi.security;

import io.valandur.webapi.WebAPIBase;
import io.valandur.webapi.logger.Logger;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import java.io.IOException;

import static io.valandur.webapi.security.SecurityService.*;

public class SecurityFilter implements Filter {

    private final SecurityService srv;
    private final Logger logger;

    public SecurityFilter() {
        var webapi = WebAPIBase.getInstance();
        this.srv = webapi.getSecurityService();
        this.logger = webapi.getLogger();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            var resume = this.filter((HttpServletRequest) request, (HttpServletResponse) response);
            if (resume) {
                chain.doFilter(request, response);
            }
        } else {
            throw new InternalServerErrorException("Could not filter: req: " + request + ", res: " + response);
        }
    }

    private boolean filter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, ACCESS_CONTROL_ORIGIN);
        response.setHeader(ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_METHODS);
        response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, ACCESS_CONTROL_HEADERS);

        // Exit early on options requests
        if (HttpMethod.OPTIONS.equalsIgnoreCase(request.getMethod())) {
            response.setStatus(Response.Status.OK.getStatusCode());
            return false;
        }

        String key = request.getHeader(API_KEY);
        if (key == null || key.isEmpty()) {
            key = request.getParameter("key");
        }
        if (key == null || key.isEmpty()) {
            key = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (key != null) key = key.substring(key.indexOf(" ") + 1);
        }

        try {
            String path = request.getPathInfo();
            String address = getRealAddress(request);
            srv.check(address, path, key, Access.WRITE);
        } catch (WebApplicationException ex) {
            response.sendError(ex.getResponse().getStatus());
            return false;
        }

        return true;
    }

    private String getRealAddress(HttpServletRequest request) {
        final String address = request.getRemoteAddr();
        String forwardedFor = request.getHeader(X_FORWARDED_FOR);
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
