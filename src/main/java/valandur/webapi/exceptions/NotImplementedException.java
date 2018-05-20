package valandur.webapi.exceptions;

import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;

public class NotImplementedException extends ServerErrorException {

    public NotImplementedException() {
        super(Response.Status.NOT_IMPLEMENTED);
    }
    public NotImplementedException(String message) {
        super(message, Response.Status.NOT_IMPLEMENTED);
    }
}
