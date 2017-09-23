package valandur.webapi.integration.webbhooks;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.WebAPI;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.permission.IPermissionService;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.json.JsonService;
import valandur.webapi.servlet.base.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebAPIServlet(basePath = "webbooks")
public class WebBookServlet extends WebAPIBaseServlet {

    private Map<String, CachedWebBook> books = new ConcurrentHashMap<>();
    private JavaType mapType = TypeFactory.defaultInstance()
            .constructMapType(ConcurrentHashMap.class, String.class, CachedWebBook.class);

    public static void onRegister() {
        JsonService json = WebAPI.getJsonService();
        json.registerSerializer(CachedWebBook.class, CachedWebBookSerializer.class);
    }


    public WebBookServlet() {
        File dir = new File("webapi/data");
        dir.mkdirs();

        try {
            File file = new File("webapi/data/webbooks.json");
            if (file.exists()) {
                String content = new String(Files.readAllBytes(file.toPath()));
                books = jsonService.toObject(content, mapType, IPermissionService.permitAllNode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBooks() {
        File file = new File("webapi/data/webbooks.json");
        String json = jsonService.toString(books, false, IPermissionService.permitAllNode());

        try {
            Files.write(file.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/book", perm = "list")
    public void listWebBooks(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("books", books.values(), data.getQueryParam("details").isPresent());
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/book/:id", perm = "one")
    public void getWebBook(ServletData data, String id) {
        CachedWebBook book = books.get(id);
        if (book == null) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("book", book, true);
    }

    @WebAPIEndpoint(method = HttpMethod.GET, path = "/book/:id/html")
    @WebAPIEndpoint(method = HttpMethod.POST, path = "/book/:id/html")
    public void getWebBookContent(ServletData data, String id) {
        CachedWebBook book = books.get(id);
        if (book == null) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
            return;
        }

        try {
            data.setContentType("text/html; charset=utf-8");
            data.getWriter().write(book.generateHtml());
            data.setDone();
        } catch (IOException e) {
            e.printStackTrace();
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not display web book");
        }
    }

    @WebAPIEndpoint(method = HttpMethod.POST, path = "/book", perm = "create")
    public void createWebBook(ServletData data) {
        JsonNode body = data.getRequestBody();

        if (!body.has("id")) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "A book needs at least an id");
            return;
        }

        String id = body.get("id").asText();
        if (id.isEmpty()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "A book needs a non-empty id");
            return;
        }

        if (books.containsKey(id)) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "A book with this id already exists");
            return;
        }

        String title = "";
        if (body.has("title")) {
            title = body.get("title").asText();
        }

        List<String> lines = new ArrayList<>();
        if (body.has("lines")) {
            body.get("lines").forEach(n -> lines.add(n.asText()));
        }

        CachedWebBook book = new CachedWebBook(id, title, lines);

        books.put(id, book);
        saveBooks();

        data.addJson("ok", true, false);
        data.addJson("book", book, true);
    }

    @WebAPIEndpoint(method = HttpMethod.PUT, path = "/book/:id", perm = "change")
    public void changeWebBook(ServletData data, String id) {
        CachedWebBook book = books.get(id);
        if (book == null) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
            return;
        }

        JsonNode body = data.getRequestBody();

        String title = book.getTitle();
        if (body.has("title")) {
            title = body.get("title").asText();
        }

        List<String> lines = new ArrayList<>();
        if (body.has("lines")) {
            body.get("lines").forEach(n -> lines.add(n.asText()));
        }

        book = new CachedWebBook(id, title, lines);

        books.put(id, book);
        saveBooks();

        data.addJson("ok", true, false);
        data.addJson("book", book, true);
    }

    @WebAPIEndpoint(method = HttpMethod.DELETE, path = "/book/:id", perm = "delete")
    public void deleteWebBook(ServletData data, String id) {
        CachedWebBook book = books.get(id);
        if (book == null) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
            return;
        }

        books.remove(book.getId());
        saveBooks();

        data.addJson("ok", true, false);
        data.addJson("book", book, true);
    }
}
