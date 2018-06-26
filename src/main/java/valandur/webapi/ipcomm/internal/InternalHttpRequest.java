package valandur.webapi.ipcomm.internal;

import org.eclipse.jetty.server.HttpChannelState;
import org.eclipse.jetty.server.Request;
import valandur.webapi.ipcomm.IPRequest;

import javax.servlet.DispatcherType;
import javax.servlet.ServletInputStream;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

public class InternalHttpRequest extends Request {

    private IPRequest msg;
    private HttpChannelState state;
    private InternalInputStream stream;


    public InternalHttpRequest(IPRequest msg) {
        super(null, null);

        this.msg = msg;
        this.state = new InternalChannelState();
        this.stream = new InternalInputStream(msg.getBody());
    }

    @Override
    public String getMethod() {
        return msg.getMethod();
    }

    @Override
    public String getHeader(String name) {
        return msg.getHeaders().get(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return Collections.enumeration(Collections.singleton(msg.getHeaders().get(name)));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(msg.getHeaders().keySet());
    }

    @Override
    public String getRemoteAddr() {
        return msg.getRemoteAddr();
    }

    @Override
    public String getQueryString() {
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : msg.getQueryParams().entrySet()) {
            query.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return query.toString();
    }

    @Override
    public HttpChannelState getHttpChannelState() {
        return this.state;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return DispatcherType.REQUEST;
    }

    @Override
    public String getContentType() {
        return getHeader(HttpHeaders.CONTENT_TYPE);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return stream;
    }

    @Override
    public String getRequestURI() {
        return msg.getPath();
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(msg.getPath());
    }
}
