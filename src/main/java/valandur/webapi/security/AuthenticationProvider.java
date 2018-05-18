package valandur.webapi.security;

import com.google.common.net.HttpHeaders;
import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.ExplicitDetails;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.api.util.TreeNode;
import valandur.webapi.config.PermissionConfig;
import valandur.webapi.user.UserPermissionStruct;
import valandur.webapi.util.SubnetUtils;
import valandur.webapi.util.Util;

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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationProvider implements ContainerRequestFilter {

    private static final String configFileName = "permissions.conf";

    private static final String API_KEY_HEADER = "X-WEBAPI-KEY";
    private static final String DEFAULT_KEY = "__DEFAULT__";

    private static String ACCESS_CONTROL_ORIGIN = "*";
    private static final String ACCESS_CONTROL_METHODS = "GET,PUT,POST,DELETE,OPTIONS";
    private static final String ACCESS_CONTROL_HEADERS = "origin, content-type, x-webapi-key";

    private static Set<String> allowedProxyIps = new HashSet<>();
    private static Set<SubnetUtils.SubnetInfo> allowedProxyCidrs = new HashSet<>();

    private static Map<String, Double> lastCall = new ConcurrentHashMap<>();
    private static long start = System.nanoTime();
    private static AtomicLong calls = new AtomicLong(0);

    private static PermissionConfig config;
    private static PermissionService permissionService;

    private static PermissionStruct defaultPerms;
    private static Map<String, PermissionStruct> permMap = new HashMap<>();

    private static Map<String, List<String>> tempUsersKeyMap = new HashMap<>();
    private static Map<String, UserPermissionStruct> tempKeyMap = new HashMap<>();

    @Context
    private ResourceInfo resourceInfo;

    @Context
    private HttpServletRequest request;

    @Context
    private HttpServletResponse response;


    public static void init() {
        Logger logger = WebAPI.getLogger();
        logger.info("Loading keys & permissions...");

        permissionService = WebAPI.getPermissionService();
        start = System.nanoTime();

        config = Util.loadConfig(configFileName, new PermissionConfig());
        defaultPerms = config.def;

        for (String proxy : config.allowedProxies) {
            if (proxy.contains("/")) {
                SubnetUtils utils = new SubnetUtils(proxy);
                utils.setInclusiveHostCount(true);
                allowedProxyCidrs.add(utils.getInfo());
            } else {
                allowedProxyIps.add(proxy);
            }
        }

        permMap.clear();
        for (Map.Entry<String, PermissionStruct> entry : config.keys.entrySet()) {
            String key = entry.getKey();
            if (key == null || key.isEmpty()) {
                logger.error("SKIPPING KEY-PERMISSION MAPPING WITH INVALID KEY");
                continue;
            }
            if (key.equalsIgnoreCase("ADMIN") || key.equalsIgnoreCase("USER") ||
                    key.equalsIgnoreCase("7S%M2FYp9NYT^Ozg")) {
                logger.error("YOU STILL HAVE SOME DEFAULT KEYS IN YOUR PERMSSIONS.CONF! " +
                        "PLESAE CHANGE OR DEACTIVATE THEM IMMEDIATELY!");
                logger.error("THE KEY '" + key + "' WILL BE SKIPPED!");
                continue;
            }
            if (key.length() < 8) {
                logger.error("YOU HAVE A KEY WITH LESS THAN 8 CHARACTERS! KEYS ARE RECOMMNDED TO BE AT " +
                        "LEAST 16 CHARACTERS AND RANDOMLY GENERATED!");
                logger.error("THE KEY '" + key + "' WILL BE SKIPPED!");
                continue;
            }
            permMap.put(key, entry.getValue());
        }

        ACCESS_CONTROL_ORIGIN = config.accessControlOrigin;
    }
    public static double getAverageCallsPerSecond() {
        double timeDiff = (System.nanoTime() - start) / 1000000000d;
        return calls.get() / timeDiff;
    }

    public static void addTempKey(String key, UserPermissionStruct user) {
        if (!tempUsersKeyMap.containsKey(user.getName())) {
            tempUsersKeyMap.put(user.getName(), new ArrayList<>());
        }
        tempUsersKeyMap.get(user.getName()).add(key);
        tempKeyMap.put(key, user);
    }
    public static void removeTempKey(String key) {
        UserPermissionStruct user = tempKeyMap.remove(key);
        if (user != null) {
            tempUsersKeyMap.get(user.getName()).remove(key);
        }
    }
    public static void updateAllFrom(UserPermissionStruct user) {
        List<String> keys = tempUsersKeyMap.get(user.getName());
        if (keys != null) {
            keys.forEach(k -> tempKeyMap.put(k, user));
        }
    }
    public static void removeAllFrom(String username) {
        List<String> keys = tempUsersKeyMap.remove(username);
        if (keys != null) {
            keys.forEach(k -> tempKeyMap.remove(k));
        }
    }

    public static void toggleBlacklist(boolean enable) {
        config.useBlacklist = enable;
        config.save();
    }
    public static void addToBlacklist(String ip) {
        config.blacklist.add(ip);
        config.save();
    }
    public static void removeFromBlacklist(String ip) {
        config.blacklist.remove(ip);
        config.save();
    }

    public static void toggleWhitelist(boolean enable) {
        config.useWhitelist = enable;
        config.save();
    }
    public static void addToWhitelist(String ip) {
        config.whitelist.add(ip);
        config.save();
    }
    public static void removeFromWhitelist(String ip) {
        config.whitelist.remove(ip);
        config.save();
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

        if (config.useWhitelist && !config.whitelist.contains(addr)) {
            WebAPI.getLogger().warn(addr + " is not on whitelist: " + target);
            throw new ForbiddenException();
        } else if (config.useBlacklist && config.blacklist.contains(addr)) {
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
            permStruct = permMap.get(key);
            if (permStruct == null) {
                permStruct = tempKeyMap.get(key);
            }
            // If the user provided a key and it's invalid, then throw an exception
            if (permStruct == null) {
                throw new ForbiddenException("Invalid api key");
            } else {
                // Makes sure that we have the correct key in case this PermStruct
                // is from a logged-in user, who usually only has a username and password.
                permStruct = permStruct.withKey(key);
            }
        } else {
            key = DEFAULT_KEY;
            permStruct = defaultPerms;
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

            TreeNode methodPerms = permissionService.subPermissions(perms, reqPermList);
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
        if (!config.allowedProxies.contains(addr)) {
            WebAPI.getLogger().warn(addr + " sent " + HttpHeaders.X_FORWARDED_FOR +
                    " header, but is not a proxy. Header will be ignored!");
            return addr;
        }

        String[] ips = forwardedFor.split(",");

        // Traverse the X-Forwarded-For header backwards and take the first IP that we don't trust.
        for (int i = ips.length - 1; i >= 0; i--) {
            String ip = ips[i].trim();
            if (allowedProxyIps.contains(ip)) {
                continue;
            }
            boolean found = false;
            for (SubnetUtils.SubnetInfo cidr : allowedProxyCidrs) {
                if (cidr.isInRange(ip)) {
                    found = true;
                    break;
                }
            }
            if (found) {
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
