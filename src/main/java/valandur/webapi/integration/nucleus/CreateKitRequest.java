package valandur.webapi.integration.nucleus;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public class CreateKitRequest extends BaseKitRequest {

    @JsonDeserialize
    private String name;
    public String getName() {
        return name != null ? name : "";
    }
}
