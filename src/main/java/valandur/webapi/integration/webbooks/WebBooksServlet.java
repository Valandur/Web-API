package valandur.webapi.integration.webbooks;

import com.google.common.reflect.TypeToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import valandur.webapi.servlet.base.BaseServlet;
import valandur.webapi.servlet.base.Permission;
import valandur.webapi.util.Util;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("web-books")
@Api(
        tags = { "Web Books" },
        value = "Create, edit and delete web books that have unique links which work on your server in-game.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class WebBooksServlet extends BaseServlet {

    private static final String configFileName = "web-books.conf";

    private Map<String, WebBook> books = new ConcurrentHashMap<>();

    private static WebBooksConfig config;


    public WebBooksServlet() {
        config = Util.loadConfig(configFileName, new WebBooksConfig());
        books = config.books;
    }

    public static void onRegister() {
        TypeSerializers.getDefaultSerializers().registerType(
                TypeToken.of(WebBook.class), new WebBookConfigSerializer());
    }

    private void saveBooks() {
        config.save();
    }

    @GET
    @Path("/book")
    @Permission({ "book", "list" })
    @ApiOperation(
            value = "List books",
            notes = "Get a list of all the web books on the server.")
    public Collection<WebBook> listWebBooks() {
        return books.values();
    }

    @GET
    @Path("/book/{id}")
    @Permission({ "book", "one" })
    @ApiOperation(
            value = "Get a book",
            notes = "Get detailed information about a web book.")
    public WebBook getWebBook(@PathParam("id") String id)
            throws NotFoundException {
        WebBook book = books.get(id);
        if (book == null) {
            throw new NotFoundException("Book with id " + id + " not found");
        }

        return book;
    }

    @GET
    @Path("/book/{id}/html")
    @ApiOperation(
            value = "Book HTML",
            notes = "Get the web book content as HTML.")
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.TEXT_HTML)
    public String getWebBookContent(@PathParam("id") String id)
            throws NotFoundException {
        WebBook book = books.get(id);
        if (book == null) {
            throw new NotFoundException("Book with id " + id + " not found");
        }

        return book.getHtml();
    }

    @POST
    @Path("/book/{id}/html")
    @ApiOperation(
            value = "Book HTML",
            notes = "Get the web book content as HTML.")
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces(MediaType.TEXT_HTML)
    public String getWebBookContentPost(@PathParam("id") String id)
            throws NotFoundException {
        WebBook book = books.get(id);
        if (book == null) {
            throw new NotFoundException("Book with id " + id + " not found");
        }

        return book.getHtml();
    }

    @POST
    @Path("/book")
    @Permission({ "book", "create" })
    @ApiOperation(
            value = "Create a book",
            response = WebBook.class,
            notes = "Create a new web book from the specified data.")
    public Response createWebBook(WebBook book)
            throws BadRequestException, URISyntaxException {

        if (book == null) {
            throw new BadRequestException("Request body is required");
        }

        String id = book.getId();
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("The book needs an id");
        }

        if (books.containsKey(id)) {
            throw new BadRequestException("A book with this id already exists");
        }

        String title = "";
        if (book.getTitle() != null)
            title = book.getTitle();

        List<String> lines = new ArrayList<>();
        if (book.getLines() != null) {
            lines = book.getLines();
        }

        WebBook newBook = new WebBook(id, title, lines);

        books.put(id, book);
        saveBooks();

        return Response.created(new URI(null, null, newBook.getLink(), null)).entity(newBook).build();
    }

    @PUT
    @Path("/book/{id}")
    @Permission({ "book", "modify" })
    @ApiOperation(
            value = "Modify a book",
            notes = "Modify an existing book.")
    public WebBook modifyWebBook(@PathParam("id") String id, WebBook newBook)
            throws NotFoundException {

        if (newBook == null) {
            throw new BadRequestException("Request body is required");
        }

        WebBook oldBook = books.get(id);
        if (oldBook == null) {
            throw new NotFoundException("Book with id " + id + " not found");
        }

        String title = oldBook.getTitle();
        if (newBook.getTitle() != null) {
            title = newBook.getTitle();
        }

        List<String> lines = oldBook.getLines();
        if (newBook.getLines() != null) {
            lines = newBook.getLines();
        }

        WebBook book = new WebBook(id, title, lines);

        books.put(id, book);
        saveBooks();

        return book;
    }

    @DELETE
    @Path("/book/{id}")
    @Permission({ "book", "delete" })
    @ApiOperation(
            value = "Delete a book",
            notes = "Delete a web book.")
    public WebBook deleteWebBook(@PathParam("id") String id)
            throws NotFoundException {
        WebBook book = books.get(id);
        if (book == null) {
            throw new NotFoundException("Book with id " + id + " not found");
        }

        books.remove(book.getId());
        saveBooks();

        return book;
    }
}
