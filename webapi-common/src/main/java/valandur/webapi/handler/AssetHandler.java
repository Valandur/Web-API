package valandur.webapi.handler;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import valandur.webapi.IPlugin;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class AssetHandler extends AbstractHandler {
    private final IPlugin plugin;

    private String contentType;
    private String assetString;

    private Function<String, Function<byte[], byte[]>> assetFunc;

    private String folderPath;
    private Map<String, CachedAsset> cachedAssets = new HashMap<>();

    public AssetHandler(IPlugin plugin, String folderPath) {
        this.plugin = plugin;

        if (folderPath.contains(".")) {
            Optional<String> asset = this.plugin.getAssetContent(folderPath).map(String::new);
            if (asset.isPresent()) {
                this.assetString = asset.get();
                this.contentType = guessContentType(folderPath);
            }
        } else {
            this.folderPath = folderPath;
        }
    }
    public AssetHandler(IPlugin plugin, String folderPath, Function<String, Function<byte[], byte[]>> processAssets) {
        this.plugin = plugin;
        this.folderPath = folderPath;
        this.assetFunc = processAssets;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.addHeader("Access-Control-Allow-Origin","*");
        response.addHeader("Access-Control-Allow-Methods","GET,PUT,POST,DELETE,OPTIONS");
        response.addHeader("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept, X-WEBAPI-KEY");

        if (assetString != null) {
            response.setContentType(contentType);
            response.setStatus(HttpServletResponse.SC_OK);

            PrintWriter out = response.getWriter();
            out.print(assetString);
            baseRequest.setHandled(true);
        } else {
            if (target.isEmpty() || target.equalsIgnoreCase("/") || !target.contains(".")) {
                target = "index.html";
            }

            String path = (folderPath + "/" + target).replace("//", "/");

            ServletOutputStream stream = response.getOutputStream();
            if (cachedAssets.containsKey(path)) {
                CachedAsset asset = cachedAssets.get(path);

                response.setContentType(asset.type);
                response.setStatus(HttpServletResponse.SC_OK);
                stream.write(asset.data);
                baseRequest.setHandled(true);
                return;
            }

            Optional<byte[]> asset = this.plugin.getAssetContent(path);
            if (asset.isPresent()) {
                byte[] data = asset.get();
                if (assetFunc != null) {
                    Function<byte[], byte[]> func = assetFunc.apply(path);
                    if (func != null) {
                        data = func.apply(data);
                    }
                }

                String type = guessContentType(path);
                cachedAssets.put(path, new CachedAsset(type, data));

                response.setContentType(type);
                response.setStatus(HttpServletResponse.SC_OK);
                stream.write(data);
            } else {
                this.plugin.getLogger().warn("Could not load asset: " + path);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            }

            baseRequest.setHandled(true);
        }
    }

    private String guessContentType(String path) {
        if (path.endsWith(".json")) {
            return "application/json; charset=utf-8";
        } else if (path.endsWith(".yaml")) {
            return "application/x-yaml; charset=utf-8";
        } else if (path.endsWith(".html")) {
            return "text/html; charset=utf-8";
        } else if (path.endsWith(".js")) {
            return "application/javascript; charset=utf-8";
        } else if (path.endsWith(".png")){
            return "image/png";
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (path.endsWith(".bmp")) {
            return "image/bmp";
        } else if (path.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }

        return "text/plain";
    }

    private class CachedAsset {
        public String type;
        public byte[] data;

        public CachedAsset(String type, byte[] data) {
            this.type = type;
            this.data = data;
        }
    }
}
