package valandur.webapi.servlet;

import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.api.message.IMessage;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.IServletData;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.message.Message;
import valandur.webapi.servlet.base.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Servlet(basePath = "message")
public class MessageServlet extends BaseServlet {

    @Endpoint(method = HttpMethod.GET, path = "/", perm = "list")
    public void getMessages(IServletData data) {
        List<IMessage> msgs = messageService.getMessages();

        data.addData("ok", true, false);
        data.addData("messages", msgs, data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.GET, path = "/:uuid", perm = "get")
    public void getMessage(IServletData data, UUID uuid) {
        Optional<IMessage> optMsg = messageService.getMessage(uuid);
        if (!optMsg.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Message with uuid " + uuid + " not found");
            return;
        }

        data.addData("ok", true, false);
        data.addData("message", optMsg.get(), true);
    }

    @Endpoint(method = HttpMethod.POST, path = "/", perm = "create")
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

        boolean allSent = messageService.sendMessage(msg);

        if (allSent) {
            data.setStatus(HttpServletResponse.SC_CREATED);
            data.setHeader("Location", msg.getLink());
        }

        data.addData("ok", allSent, false);
        data.addData("message", msg, true);
    }
}
