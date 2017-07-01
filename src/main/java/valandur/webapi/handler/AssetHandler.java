package valandur.webapi.handler;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;

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

    private String contentType;
    private String assetString;

    private Function<String, Function<byte[], byte[]>> assetFunc;

    private String folderPath;
    private Map<String, Tuple<String, byte[]>> cachedAssets = new HashMap<>();

    public AssetHandler(String folderPath) {
        Optional<Asset> asset = Sponge.getAssetManager().getAsset(WebAPI.getInstance(), folderPath);
        if (folderPath.contains(".") && asset.isPresent()) {
            try {
                this.assetString = asset.get().readString();
                this.contentType = guessContentType(folderPath);
            } catch (IOException ignored) {}
        } else {
            this.folderPath = folderPath;
        }
    }
    public AssetHandler(String folderPath, Function<String, Function<byte[], byte[]>> processAssets) {
        this.folderPath = folderPath;
        this.assetFunc = processAssets;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (assetString != null) {
            response.setContentType(contentType + "; charset=utf-8");
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
                Tuple<String, byte[]> asset = cachedAssets.get(path);

                response.setContentType(asset.getFirst());
                response.setStatus(HttpServletResponse.SC_OK);
                stream.write(asset.getSecond());
                baseRequest.setHandled(true);
                return;
            }

            Optional<Asset> asset = Sponge.getAssetManager().getAsset(WebAPI.getInstance(), path);
            if (asset.isPresent()) {
                byte[] data = asset.get().readBytes();
                if (assetFunc != null) {
                    Function<byte[], byte[]> func = assetFunc.apply(path);
                    if (func != null) {
                        data = func.apply(data);
                    }
                }

                String type = guessContentType(path);
                cachedAssets.put(path, new Tuple<>(type, data));

                response.setStatus(HttpServletResponse.SC_OK);
                stream.write(data);
            } else {
                WebAPI.getLogger().warn("Could not load asset: " + path);
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
}
