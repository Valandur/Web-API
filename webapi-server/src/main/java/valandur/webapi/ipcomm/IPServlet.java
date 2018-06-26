package valandur.webapi.ipcomm;

import javax.servlet.Servlet;

public interface IPServlet<T extends IPLink> extends Servlet {

    void init(T link);
}
