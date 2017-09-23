package valandur.webapi.integration.webbhooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize
@JsonIgnoreProperties(value={"html"})
public class CachedWebBook {

    @JsonDeserialize
    private String id;
    public String getId() {
        return id;
    }

    @JsonDeserialize
    private String title;
    public String getTitle() {
        return title;
    }

    @JsonDeserialize
    private List<String> lines;
    public List<String> getLines() {
        return lines;
    }


    public CachedWebBook() {
    }
    public CachedWebBook(String id, String title, List<String> lines) {
        this.id = id;
        this.title = title;
        this.lines = lines;
    }

    public String generateHtml() {
        String html = "<!DOCTYPE><html><head><title>" + title + "</title></head><body><ul class='book'>";
        for (String line : lines) {
            html += "<li>" + line + "</li>";
        }
        return html + "</ul></body></html>";
    }
}
