package valandur.webapi.integration.webbooks;

import com.fasterxml.jackson.annotation.JsonProperty;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.util.Constants;

import java.util.List;

public class WebBook {

    @JsonProperty(required = true)
    private String id;
    public String getId() {
        return id;
    }

    private String title;
    public String getTitle() {
        return title;
    }

    private List<String> lines;
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
    public String getHtml() {
        StringBuilder html = new StringBuilder("<!DOCTYPE><html><head><title>" + title + "</title></head><body><ul class='book'>");
        for (String line : lines) {
            html.append("<li>").append(line).append("</li>");
        }
        return html + "</ul></body></html>";
    }

    public String getLink() {
        return Constants.BASE_PATH + "/book/" + id;
    }
}
