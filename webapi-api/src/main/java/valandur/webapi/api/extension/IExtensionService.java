package valandur.webapi.api.extension;

import java.util.function.Consumer;

/**
 * The extension service allows loading extension to the Web-API.
 */
public interface IExtensionService {

    /**
     * Loads and compiles all extension java files from the specified package
     * @param pkg The package path, relative to the /webapi/ directory
     * @param baseClass The base class which the extension classes must extend
     * @param done A function that is applied to completely loaded extension classes.
     *             Use this to save the class or create an instance.
     * @param <T> The base type of the extension
     */
    <T> void loadPlugins(String pkg, Class<T> baseClass, Consumer<Class<T>> done);
}
