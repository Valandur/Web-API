package valandur.webapi.misc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.spongepowered.api.util.Tuple;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class Util {

    public static String[] getPathParts(HttpServletRequest req) {
        String path = req.getPathInfo();
        if (path == null) return new String[] { };
        return path.substring(1).split("/");
    }

    public static Map<String, String> getQueryParts(HttpServletRequest req) {
        Map<String, String> map = new HashMap<>();
        String[] splits = req.getQueryString().split("&");
        for (String split : splits) {
            String[] subSplits = split.split("=");
            map.put(subSplits[0], subSplits[1]);
        }
        return map;
    }

    public static String lowerFirst(String text) {
        return Character.toLowerCase(text.charAt(0)) + text.substring(1);
    }

    public static Optional<Tuple<Class[], Object[]>> parseParams(JsonArray arr) {
        Class[] paramTypes = new Class[arr.size()];
        Object[] paramValues = new Object[arr.size()];

        try {
            for (int i = 0; i < arr.size(); i++) {
                ParamDef def = ParamDef.fromJson(arr.get(i).getAsJsonObject());
                paramTypes[i] = def.type;
                paramValues[i] = def.value;
            }
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }

        return Optional.of(new Tuple<>(paramTypes, paramValues));
    }
}
