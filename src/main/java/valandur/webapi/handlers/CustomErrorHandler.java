package valandur.webapi.handlers;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.handler.ErrorHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;

public class CustomErrorHandler extends ErrorHandler {
    @Override
    protected void handleErrorPage(HttpServletRequest request, Writer writer, int code, String message) throws IOException {
        if (message == null)
            message= HttpStatus.getMessage(code);

        writer.write("{ code: " + code + ", message: \"" + message + "\" }");
    }
}
