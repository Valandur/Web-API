package valandur.webapi.servlet;

import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.message.Message;
import valandur.webapi.servlet.base.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@WebAPIServlet(basePath = "message")
public class MessageServlet extends WebAPIBaseServlet {

    @WebAPIEndpoint(method = HttpMethod.POST, path = "/", perm = "create")
    public void sendMessage(ServletData data) {
        Optional<Message> optMsg = data.getRequestBody(Message.class);
        if (!optMsg.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid message");
            return;
        }

        Message msg = optMsg.get();

        if (msg.getTarget() == null && (msg.getTargets() == null || msg.getTargets().size() == 0)) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "You need to specify either a " +
                    "single target or a list of targets");
            return;
        }

        data.addJson("ok", messageService.sendMessage(msg), false);
    }
}
