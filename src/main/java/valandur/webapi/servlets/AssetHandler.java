package valandur.webapi.servlets;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import valandur.webapi.WebAPI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

public class AssetHandler extends AbstractHandler {

    private String contentType;
    private String assetString;

    public AssetHandler(String assetString, String contentType) {
        this.assetString = assetString;
        this.contentType = contentType;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType(contentType + "; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = response.getWriter();
        out.print(assetString);
        baseRequest.setHandled(true);
    }
}
