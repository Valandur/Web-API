package valandur.webapi.servlet.message;

import valandur.webapi.api.annotation.WebAPIRoute;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.IServlet;
import valandur.webapi.message.Message;
import valandur.webapi.message.Messages;
import valandur.webapi.servlet.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@WebAPIServlet(basePath = "message")
public class MessageServlet implements IServlet {

    @WebAPIRoute(method = "POST", path = "/", perm = "create")
    public void sendMessage(ServletData data) {
        Optional<Message> msg = data.getRequestBody(Message.class);
        if (!msg.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid message");
            return;
        }

        data.addJson("ok", Messages.sendMessage(msg.get()), false);
    }
}
