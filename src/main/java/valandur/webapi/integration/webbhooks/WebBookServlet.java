package valandur.webapi.integration.webbhooks;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.WebAPI;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.json.JsonService;
import valandur.webapi.servlet.base.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebAPIServlet(basePath = "webbook")
public class WebBookServlet extends WebAPIBaseServlet {

    private Map<String, CachedWebBook> books = new ConcurrentHashMap<>();


    public static void onRegister() {
        JsonService json = WebAPI.getJsonService();
        json.registerSerializer(CachedWebBook.class, CachedWebBookSerializer.class);
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/", perm = "list")
    public void listWebBooks(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("books", books.values(), data.getQueryParam("details").isPresent());
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/:id", perm = "one")
    public void getWebBook(ServletData data, String id) {
        CachedWebBook book = books.get(id);
        if (book == null) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("book", book, true);
    }

    @WebAPIEndpoint(method = HttpMethod.POST, path = "/:id/html")
    public void getWebBookContent(ServletData data, String id) {
        CachedWebBook book = books.get(id);
        if (book == null) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
            return;
        }

        try {
            data.setContentType("text/html; charset=utf-8");
            data.getWriter().write(book.getBody());
            data.setDone();
        } catch (IOException e) {
            e.printStackTrace();
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not display web book");
        }
    }

    @WebAPIEndpoint(method = HttpMethod.POST, path = "/", perm = "create")
    public void createWebBook(ServletData data) {
        JsonNode body = data.getRequestBody();
        String id = body.get("id").asText();

        if (books.containsKey(id)) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "A book with this id already exists");
            return;
        }

        String bookBody = body.get("body").asText();

        CachedWebBook book = new CachedWebBook(id, bookBody);

        books.put(id, book);

        data.addJson("ok", true, false);
        data.addJson("book", book, true);
    }

    @WebAPIEndpoint(method = HttpMethod.PUT, path = "/:id", perm = "change")
    public void changeWebBook(ServletData data, String id) {
        CachedWebBook book = books.get(id);
        if (book == null) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
            return;
        }

        JsonNode body = data.getRequestBody();

        String bookBody = body.get("body").asText();

        book = new CachedWebBook(id, bookBody);

        books.put(id, book);

        data.addJson("ok", true, false);
        data.addJson("book", book, true);
    }

    @WebAPIEndpoint(method = HttpMethod.DELETE, path = "/:id", perm = "delete")
    public void deleteWebBook(ServletData data, String id) {
        CachedWebBook book = books.get(id);
        if (book == null) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
            return;
        }

        books.remove(book.getId());

        data.addJson("ok", true, false);
        data.addJson("book", book, true);
    }
}
