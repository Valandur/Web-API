package valandur.webapi.servlet;

import valandur.webapi.annotation.WebAPISpec;
import valandur.webapi.message.Message;
import valandur.webapi.message.Messages;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class MessageServlet extends WebAPIServlet {

    @WebAPISpec(method = "POST", path = "/", perm = "message.post")
    public void sendMessage(ServletData data) {
        Optional<Message> msg = data.getRequestBody(Message.class);
        if (!msg.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid message");
            return;
        }

        data.addJson("ok", Messages.sendMessage(msg.get()), false);
    }
}
