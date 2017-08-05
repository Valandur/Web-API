package valandur.webapi.api.cache.plugin;

import valandur.webapi.api.cache.ICachedObject;

import java.util.List;
import java.util.Set;

public interface ICachedPluginContainer extends ICachedObject {

    String getId();

    String getName();

    String getVersion();

    String getDescription();

    String getUrl();

    List<String> getAuthors();

    Set<ICachedPluginDependency> getDependencies();
}
