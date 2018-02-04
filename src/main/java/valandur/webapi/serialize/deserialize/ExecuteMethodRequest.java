package valandur.webapi.serialize.deserialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("ExecuteMethodRequest")
public class ExecuteMethodRequest {

    private String method;
    @ApiModelProperty(value = "The method that is executed", required = true)
    public String getMethod() {
        return method;
    }

    private List<ExecuteMethodParam> parameters;
    @ApiModelProperty("The parameters of the method (if applicable)")
    public List<ExecuteMethodParam> getParameters() {
        return parameters;
    }
}
