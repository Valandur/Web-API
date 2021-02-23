package io.valandur.webapi.web;


import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ErrorHandler extends org.eclipse.jetty.server.handler.ErrorHandler implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        int status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

        if (exception instanceof WebApplicationException) {
            status = ((WebApplicationException) exception).getResponse().getStatus();
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

    public static class ErrorMessage {
        public int status;
        public String error;

        public ErrorMessage(int status, String error) {
            this.status = status;
            this.error = error;
        }
    }
}