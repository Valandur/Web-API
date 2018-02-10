package valandur.webapi.handler;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ErrorHandler extends org.eclipse.jetty.server.handler.ErrorHandler implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        int status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        if (exception instanceof WebApplicationException) {
            status = ((WebApplicationException)exception).getResponse().getStatus();
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

    @ApiModel("ErrorMessage")
    public static class ErrorMessage {
        @ApiModelProperty("The status code of the error. This is also returned as the HTTP status code.")
        public int status;

        @ApiModelProperty("A description of the error that occured")
        public String error;

        public ErrorMessage(int status, String error) {
            this.status = status;
            this.error = error;
        }
    }
}
