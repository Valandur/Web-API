package valandur.webapi.handler;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
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

public class AssetHandler extends AbstractHandler {

    private String contentType;
    private String assetString;

    private String folderPath;
    private Map<String, byte[]> cachedAssets = new HashMap<>();

    public AssetHandler(String folderPath) {
        this.folderPath = folderPath;
    }
    public AssetHandler(String assetString, String contentType) {
        this.assetString = assetString;
        this.contentType = contentType;
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
            if (target.isEmpty() || target.equalsIgnoreCase("/")) {
                target = "index.html";
            }

            String path = folderPath + "/" + target;

            ServletOutputStream stream = response.getOutputStream();
            if (cachedAssets.containsKey(path)) {
                response.setStatus(HttpServletResponse.SC_OK);
                stream.write(cachedAssets.get(path));
                baseRequest.setHandled(true);
                return;
            }

            Optional<Asset> asset = Sponge.getAssetManager().getAsset(WebAPI.getInstance(), path);
            if (asset.isPresent()) {
                byte[] bytes = asset.get().readBytes();
                cachedAssets.put(path, bytes);

                response.setStatus(HttpServletResponse.SC_OK);
                stream.write(bytes);
                baseRequest.setHandled(true);
            } else {
                handle("index.html", baseRequest, request, response);
            }
        }
    }
}
