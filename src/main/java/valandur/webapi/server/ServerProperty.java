package valandur.webapi.server;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("ServerProperty")
public class ServerProperty {

    private String key;
    @ApiModelProperty(value = "The key of the server property", required = true)
    public String getKey() {
        return key;
    }

    private String value;
    @ApiModelProperty(value = "The value of the server property", required = true)
    public String getValue() {
        return value;
    }


    public ServerProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
