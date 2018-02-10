package valandur.webapi.serialize.deserialize;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.util.Tuple;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
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


    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public Tuple<Class[], Object[]> getParsedParameters() {
        Class[] paramTypes = new Class[parameters.size()];
        Object[] paramValues = new Object[parameters.size()];

        try {
            for (int i = 0; i < parameters.size(); i++) {
                Tuple<Class, Object> tup = parameters.get(i).toObject();
                paramTypes[i] = tup.getFirst();
                paramValues[i] = tup.getSecond();
            }
        } catch (ClassNotFoundException e) {
            throw new NotFoundException("Class could not be found: " + e.getMessage());
        } catch (NoSuchFieldException e) {
            throw new NotFoundException("Field could not be found: " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new ForbiddenException("Access not allowed: " + e.getMessage());
        }

        return new Tuple<>(paramTypes, paramValues);
    }
}
