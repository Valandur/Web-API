package valandur.webapi.servlet.message;

import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.message.Message;
import valandur.webapi.servlet.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@WebAPIServlet(basePath = "message")
public class MessageServlet extends WebAPIBaseServlet {

    @WebAPIEndpoint(method = HttpMethod.POST, path = "/", perm = "create")
    public void sendMessage(ServletData data) {
        Optional<Message> msg = data.getRequestBody(Message.class);
        if (!msg.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid message");
            return;
        }

        data.addJson("ok", messageService.sendMessage(msg.get()), false);
    }
}
