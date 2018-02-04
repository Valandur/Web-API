package valandur.webapi.serialize.deserialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import static valandur.webapi.util.Util.ParameterType;

@ApiModel("ExecuteMethodParam")
public class ExecuteMethodParam {

    private ParameterType type;
    @ApiModelProperty(value = "The type of the parameter", required = true)
    public ParameterType getType() {
        return type;
    }

    private String value;
    @ApiModelProperty(value = "The value of the parameter", required = true)
    public String getValue() {
        return value;
    }
}
