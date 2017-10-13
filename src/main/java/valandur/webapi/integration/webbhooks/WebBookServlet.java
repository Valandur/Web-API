package valandur.webapi.integration.webbhooks;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.api.permission.IPermissionService;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.IServletData;
import valandur.webapi.api.servlet.Servlet;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Servlet(basePath = "webbooks")
public class WebBookServlet extends BaseServlet {

    private Map<String, WebBook> books = new ConcurrentHashMap<>();


    public WebBookServlet() {
        File dir = new File("webapi/data");
        dir.mkdirs();

        try {
            File file = new File("webapi/data/webbooks.json");
            if (file.exists()) {
                String content = new String(Files.readAllBytes(file.toPath()));
                JavaType mapType = TypeFactory.defaultInstance()
                        .constructMapType(ConcurrentHashMap.class, String.class, WebBook.class);
                books = serializeService.deserialize(content, false, mapType, IPermissionService.permitAllNode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBooks() {
        File file = new File("webapi/data/webbooks.json");
        String json = serializeService.toString(books, false, false, IPermissionService.permitAllNode());

        try {
            Files.write(file.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Endpoint(method = HttpMethod.GET, path = "/book", perm = "list")
    public void listWebBooks(IServletData data) {
        data.addData("ok", true, false);
        data.addData("books", books.values(), data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.GET, path = "/book/:id", perm = "one")
    public void getWebBook(IServletData data, String id) {
        WebBook book = books.get(id);
        if (book == null) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
            return;
        }

        data.addData("ok", true, false);
        data.addData("book", book, true);
    }

    @Endpoint(method = HttpMethod.GET, path = "/book/:id/html")
    @Endpoint(method = HttpMethod.POST, path = "/book/:id/html")
    public void getWebBookContent(IServletData data, String id) {
        WebBook book = books.get(id);
        if (book == null) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
            return;
        }

        try {
            data.setContentType("text/html; charset=utf-8");
            data.getOutputStream().write(book.getHtml().getBytes(Charset.forName("UTF-8")));
            data.setDone();
        } catch (IOException e) {
            e.printStackTrace();
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not display web book");
        }
    }

    @Endpoint(method = HttpMethod.POST, path = "/book", perm = "create")
    public void createWebBook(IServletData data) {
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

        WebBook book = new WebBook(id, title, lines);

        books.put(id, book);
        saveBooks();

        data.addData("ok", true, false);
        data.addData("book", book, true);
    }

    @Endpoint(method = HttpMethod.PUT, path = "/book/:id", perm = "change")
    public void changeWebBook(IServletData data, String id) {
        WebBook book = books.get(id);
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

        book = new WebBook(id, title, lines);

        books.put(id, book);
        saveBooks();

        data.addData("ok", true, false);
        data.addData("book", book, true);
    }

    @Endpoint(method = HttpMethod.DELETE, path = "/book/:id", perm = "delete")
    public void deleteWebBook(IServletData data, String id) {
        WebBook book = books.get(id);
        if (book == null) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found");
            return;
        }

        books.remove(book.getId());
        saveBooks();

        data.addData("ok", true, false);
        data.addData("book", book, true);
    }
}
