package valandur.webapi.integration.webbooks;

import com.google.common.reflect.TypeToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Permission;
import valandur.webapi.util.Util;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("web-books")
@Api(value = "web-books", tags = { "Web Books" })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class WebBookServlet extends BaseServlet {

    private static final String configFileName = "web-books.conf";

    private Map<String, WebBook> books = new ConcurrentHashMap<>();

    private static ConfigurationLoader loader;
    private static ConfigurationNode config;


    public WebBookServlet() {
        Tuple<ConfigurationLoader, ConfigurationNode> tup =
                Util.loadWithDefaults(configFileName, "defaults/" + configFileName);
        loader = tup.getFirst();
        config = tup.getSecond();

        try {
            List<WebBook> bookList = config.getNode("books").getList(TypeToken.of(WebBook.class));
            for (WebBook book : bookList) {
                books.put(book.getId(), book);
            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public static void onRegister() {
        TypeSerializers.getDefaultSerializers().registerType(
                TypeToken.of(WebBook.class), new WebBookConfigSerializer());
    }

    private void saveBooks() throws InternalServerErrorException {
        try {
            loader.save(config);
        } catch (IOException e) {
            e.printStackTrace();
            if (WebAPI.reportErrors()) WebAPI.sentryCapture(e);
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/book")
    @Permission({ "book", "list" })
    @ApiOperation(value = "List books", notes = "Get a list of all the web books on the server.")
    public Collection<WebBook> listWebBooks() {
        return books.values();
    }

    @GET
    @Path("/book/{id}")
    @Permission({ "book", "one" })
    @ApiOperation(value = "Get a book", notes = "Get detailed information about a web book.")
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
    @ApiOperation(value = "Book HTML", notes = "Get the web book content as HTML.")
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
    @Path("/book")
    @Permission({ "book", "create" })
    @ApiOperation(value = "Create a book", notes = "Create a new web book from the specified data.")
    public WebBook createWebBook(WebBook book)
            throws BadRequestException, InternalServerErrorException {
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

        return book;
    }

    @PUT
    @Path("/book/{id}")
    @Permission({ "book", "change" })
    @ApiOperation(value = "Change a book", notes = "Change an existing book.")
    public WebBook changeWebBook(@PathParam("id") String id, WebBook newBook)
            throws InternalServerErrorException, NotFoundException {
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
    @ApiOperation(value = "Delete a book", notes = "Delete a web book.")
    public WebBook deleteWebBook(@PathParam("id") String id)
            throws NotFoundException, InternalServerErrorException {
        WebBook book = books.get(id);
        if (book == null) {
            throw new NotFoundException("Book with id " + id + " not found");
        }

        books.remove(book.getId());
        saveBooks();

        return book;
    }
}
