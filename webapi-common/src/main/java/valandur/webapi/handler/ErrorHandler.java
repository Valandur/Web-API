package valandur.webapi.handler;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import valandur.webapi.IPlugin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.Writer;

@Provider
public class ErrorHandler extends org.eclipse.jetty.server.handler.ErrorHandler implements ExceptionMapper<Throwable> {

    public static IPlugin plugin;

    @Override
    public Response toResponse(Throwable exception) {
        int status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

        if (exception instanceof WebApplicationException) {
            status = ((WebApplicationException)exception).getResponse().getStatus();
        } else if (exception instanceof UnrecognizedPropertyException) {
            status = Response.Status.BAD_REQUEST.getStatusCode();
        } else {
            // Print the stack trace as this is an "unexpected" exception,
            // and we want to make sure we can track it down
            exception.printStackTrace();
        }

        return Response
                .status(status)
                .entity(new ErrorMessage(status, exception.getMessage()))
                .build();
    }

    @Override
    protected void handleErrorPage(HttpServletRequest request, Writer writer, int code, String message) throws IOException {
        if (message == null) {
            message = HttpStatus.getMessage(code);
        }

        writer.write("{\"code\":\"" + code + "\",\"message\":\"" + message + "\"}");
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.plugin.getLogger().error(target);
        super.handle(target, baseRequest, request, response);
    }

    public static class ErrorMessage {
        public int status;
        public String error;

        public ErrorMessage(int status, String error) {
            this.status = status;
            this.error = error;
        }
    }
}
