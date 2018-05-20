package valandur.webapi.serialize.objects;

import io.swagger.annotations.ApiModel;
import valandur.webapi.cache.CachedObject;

@ApiModel("ExecuteMethodResponse")
public class ExecuteMethodResponse {

    private CachedObject object;
    public CachedObject getObject() {
        return object;
    }

    private Object result;
    public Object getResult() {
        return result;
    }


    public ExecuteMethodResponse(CachedObject object, Object result) {
        this.object = object;
        this.result = result;
    }
}
