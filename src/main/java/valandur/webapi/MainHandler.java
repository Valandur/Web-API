package valandur.webapi;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.InternalServerErrorException;
import java.util.concurrent.ExecutionException;

public class MainHandler extends AbstractHandler {

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            WSSocket.emit(target).get();
            response.setStatus(Response.SC_OK);
            baseRequest.setHandled(true);
        } catch (InterruptedException | ExecutionException e) {
            throw new InternalServerErrorException(e);
        }
    }
}
