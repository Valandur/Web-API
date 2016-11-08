package valandur.webapi.handlers;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthHandler extends AbstractHandler {
    private Map<String, Tuple<String, List<String>>> authMap = new HashMap<>();

    public AuthHandler() {
        try {
            URL url = WebAPI.class.getResource("/assets/webapi/permissions.yaml");
            YAMLConfigurationLoader loader = YAMLConfigurationLoader.builder().setURL(url).build();
            ConfigurationNode permNode = loader.load();
            for (ConfigurationNode node : permNode.getChildrenList()) {
                ArrayList<String> perms = new ArrayList<>();
                for (ConfigurationNode perm : node.getNode("permissions").getChildrenList()) {
                    perms.add(perm.getString());
                }
                authMap.put(node.getNode("name").getString(), new Tuple<>(node.getNode("token").getString(), perms));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String user = request.getHeader("x-webapi-user");
        String token = request.getHeader("x-webapi-token");
        String query = request.getQueryString();

        if (user == null && query != null) {
            String[] splits = query.split("&");
            for (String split : splits) {
                String[] subSplits = split.split("=");
                if (subSplits[0].equalsIgnoreCase("user")) user = subSplits[1];
                if (subSplits[0].equalsIgnoreCase("token")) token = subSplits[1];
            }
        }

        Tuple<String, List<String>> tuple = authMap.get(user);
        if (tuple != null && tuple.getFirst().equals(token)) {
            request.setAttribute("perms", tuple.getSecond());
        }
    }
}
