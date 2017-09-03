package valandur.webapi.api.cache.misc;

import valandur.webapi.api.cache.ICachedObject;

import java.util.Map;

public interface ICachedCause extends ICachedObject {

    Map<String, Object> getCauses();
}
