package valandur.webapi.servlet;

import valandur.webapi.message.Message;
import valandur.webapi.message.Messages;
import valandur.webapi.permission.Permission;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class MessageServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "message.post")
    protected void handlePost(ServletData data) {
        Optional<Message> msg = data.getRequestBody(Message.class);
        if (!msg.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid message");
            return;
        }

        data.addJson("ok", Messages.sendMessage(msg.get()), false);
    }
}
