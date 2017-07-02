package valandur.webapi.api.cache.world;

import valandur.webapi.api.cache.ICachedObject;

import java.util.Map;

public interface ICachedGeneratorType extends ICachedObject {

    Map<String, Object> getSettings();
}
