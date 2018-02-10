package valandur.webapi.integration.webbooks;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.util.Constants;

import java.util.List;

@ApiModel("WebBooksBook")
public class WebBook {

    @JsonProperty(required = true)
    private String id;
    @ApiModelProperty(value = "The unique id of this book", required = true)
    public String getId() {
        return id;
    }

    private String title;
    @ApiModelProperty(value = "The title of this book", required = true)
    public String getTitle() {
        return title;
    }

    private List<String> lines;
    @ApiModelProperty(value = "A list of lines that make up this book", required = true)
    public List<String> getLines() {
        return lines;
    }


    public WebBook() {
    }
    public WebBook(String id, String title, List<String> lines) {
        this.id = id;
        this.title = title;
        this.lines = lines;
    }

    @JsonDetails
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ApiModelProperty("The HTML representation of this book")
    public String getHtml() {
        StringBuilder html = new StringBuilder("<!DOCTYPE><html><head><title>" + title + "</title></head><body><ul class='book'>");
        for (String line : lines) {
            html.append("<li>").append(line).append("</li>");
        }
        return html + "</ul></body></html>";
    }

    @ApiModelProperty(
            value = "The API link that can be used to obtain more information about this object",
            required = true,
            readOnly = true)
    public String getLink() {
        return Constants.BASE_PATH + "/book/" + id;
    }
}
