package valandur.webapi.serialize.objects;

import io.swagger.annotations.ApiModel;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.ICachedObject;

@ApiModel("ExecuteMethodResponse")
public class ExecuteMethodResponse {

    private ICachedObject object;
    public ICachedObject getObject() {
        return object;
    }

    private Object result;
    public Object getResult() {
        return result;
    }


    public ExecuteMethodResponse(ICachedObject object, Object result) {
        this.object = object;
        this.result = result;
    }
}
