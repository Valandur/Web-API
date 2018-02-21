package valandur.webapi.security;

import com.google.common.net.HttpHeaders;
import com.google.common.reflect.TypeToken;
import com.sun.deploy.net.HttpResponse;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.ExplicitDetails;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.api.util.TreeNode;
import valandur.webapi.user.UserPermissionStruct;
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
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationProvider implements ContainerRequestFilter {

    public static final String API_KEY_HEADER = "X-WEBAPI-KEY";
    private static final String DEFAULT_KEY = "__DEFAULT__";
    private static final String configFileName = "permissions.conf";

    private static String ACCESS_CONTROL_ORIGIN = "*";
    private static final String ACCESS_CONTROL_METHODS = "GET,PUT,POST,DELETE,OPTIONS";
    private static final String ACCESS_CONTROL_HEADERS = "Origin,Content-Type,Accept,X-Forwarded-For," + API_KEY_HEADER;

    private static Map<String, Double> lastCall = new ConcurrentHashMap<>();
    private static long start = System.nanoTime();
    private static AtomicLong calls = new AtomicLong(0);

    private static ConfigurationLoader loader;
    private static ConfigurationNode config;
    private static PermissionService permissionService;

    private static PermissionStruct defaultPerms;
    private static Map<String, PermissionStruct> permMap = new HashMap<>();

    private static Map<String, UserPermissionStruct> tempPermMap = new HashMap<>();

    private static boolean useWhitelist;
    private static List<String> whitelist = new ArrayList<>();

    private static boolean useBlacklist;
    private static List<String> blacklist = new ArrayList<>();

    private static List<String> allowedProxies = new ArrayList<>();

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

        Tuple<ConfigurationLoader, ConfigurationNode> tup =
                Util.loadWithDefaults(configFileName, "defaults/" + configFileName);
        loader = tup.getFirst();
        config = tup.getSecond();

        TreeNode<String, Boolean> defs = permissionService.permissionTreeFromConfig(config.getNode("default", "permissions"));
        int defLimit = config.getNode("default", "rateLimit").getInt();
        defaultPerms = new PermissionStruct(defs, defLimit);

        permMap.clear();
        for (ConfigurationNode node : config.getNode("keys").getChildrenList()) {
            String key = node.getNode("key").getString();
            if (!node.getNode("enabled").getBoolean()) {
                logger.info("Skipping disabled key " + key);
                continue;
            }
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
            TreeNode<String, Boolean> perms = permissionService.permissionTreeFromConfig(node.getNode("permissions"));
            permMap.put(key, new PermissionStruct(key, perms, node.getNode("rateLimit").getInt()));
        }

        try {
            // Load whitelist
            useWhitelist = config.getNode("useWhitelist").getBoolean();
            whitelist = config.getNode("whitelist").getList(TypeToken.of(String.class));

            // Load blacklist
            useBlacklist = config.getNode("useBlacklist").getBoolean();
            blacklist = config.getNode("blacklist").getList(TypeToken.of(String.class));

            // Allowed proxies
            allowedProxies = config.getNode("allowedProxies").getList(TypeToken.of(String.class));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        ACCESS_CONTROL_ORIGIN = config.getNode("accessControlOrigin").getString(ACCESS_CONTROL_ORIGIN);
    }
    public static double getAverageCallsPerSecond() {
        double timeDiff = (System.nanoTime() - start) / 1000000000d;
        return calls.get() / timeDiff;
    }
    private static void setAndSaveConfig(String node, Object value) {
        config.getNode(node).setValue(value);

        try {
            loader.save(config);
        } catch (IOException e) {
            e.printStackTrace();
            if (WebAPI.reportErrors()) WebAPI.sentryCapture(e);
        }
    }

    public static void addTempPerm(String key, UserPermissionStruct perms) {
        tempPermMap.put(key, perms);
    }

    public static void toggleBlacklist(boolean enable) {
        useBlacklist = enable;
        setAndSaveConfig("useBlacklist", enable);
    }
    public static void addToBlacklist(String ip) {
        blacklist.add(ip);
        setAndSaveConfig("blacklist", blacklist);
    }
    public static void removeFromBlacklist(String ip) {
        blacklist.remove(ip);
        setAndSaveConfig("blacklist", blacklist);
    }

    public static void toggleWhitelist(boolean enable) {
        useWhitelist = enable;
        setAndSaveConfig("useWhitelist", enable);
    }
    public static void addToWhitelist(String ip) {
        whitelist.add(ip);
        setAndSaveConfig("whitelist", whitelist);
    }
    public static void removeFromWhitelist(String ip) {
        whitelist.remove(ip);
        setAndSaveConfig("whitelist", whitelist);
    }

    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        String addr = getRealAddr(request);
        String target = context.getUriInfo().getPath();

        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ACCESS_CONTROL_ORIGIN);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_METHODS);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, ACCESS_CONTROL_HEADERS);

        if (useWhitelist && !whitelist.contains(addr)) {
            WebAPI.getLogger().warn(addr + " is not on whitelist: " + target);
            throw new ForbiddenException();
        } else if (useBlacklist && blacklist.contains(addr)) {
            WebAPI.getLogger().warn(addr + " is on blacklist: " + target);
            throw new ForbiddenException();
        }

        // Exit early on options requests
        if (HttpMethod.OPTIONS.asString().equalsIgnoreCase(context.getMethod())) {
            context.abortWith(Response.status(HttpStatus.OK_200).build());
            return;
        }

        String key = context.getHeaderString(API_KEY_HEADER);
        if (key == null || key.isEmpty()) {
            key = context.getUriInfo().getQueryParameters().getFirst("key");
        }
        if (key == null || key.isEmpty()) {
            key = context.getHeaderString(HttpHeaders.AUTHORIZATION);
            if (key != null) key = key.substring(key.indexOf(" ") + 1);
        }

        PermissionStruct permStruct = null;
        if (key != null) {
            permStruct = permMap.get(key);
            if (permStruct == null) {
                permStruct = tempPermMap.get(key);
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

        Class c = resourceInfo.getResourceClass();
        String basePath = resourceInfo.getResourceClass().getAnnotation(Path.class).value();
        TreeNode<String, Boolean> perms = permStruct.getPermissions();

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

            TreeNode<String, Boolean> methodPerms = permissionService.subPermissions(perms, reqPermList);
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
        if (!allowedProxies.contains(addr)) {
            WebAPI.getLogger().warn(addr + " sent " + HttpHeaders.X_FORWARDED_FOR +
                    " header, but is not a proxy. Header will be ignored!");
            return addr;
        }

        String[] ips = forwardedFor.split(",");

        // Traverse the X-Forwarded-For header backwards and take the first IP that we don't trust.
        for (int i = ips.length - 1; i >= 0; i--) {
            if (!allowedProxies.contains(ips[i].trim())) {
                if (i > 0) {
                    WebAPI.getLogger().warn(ips[i].trim() + " sent " + HttpHeaders.X_FORWARDED_FOR +
                            " header, but is not a proxy. Header will be ignored!");
                }
                return ips[i];
            }
        }

        // We usually shouldn't get here, but if we don't it means we trusted all proxy ips, so just
        // return the last one of those.
        return ips[ips.length - 1];
    }
}
