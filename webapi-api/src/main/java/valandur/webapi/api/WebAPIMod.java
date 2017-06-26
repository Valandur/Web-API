package valandur.webapi.api;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(
        id = WebAPIMod.ID,
        version = WebAPIMod.VERSION,
        name = WebAPIMod.NAME,
        url = WebAPIMod.URL,
        description = WebAPIMod.DESCRIPTION,
        authors = {
                "Valandur"
        }
)
public class WebAPIMod {
    public static final String ID = "webapi-api";
    public static final String NAME = "Web-API API";
    public static final String VERSION = "@version@";
    public static final String DESCRIPTION = "API interface for the WebAPI";
    public static final String URL = "https://github.com/Valandur/Web-API";

    @Inject
    private Logger logger;

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
        logger.info(NAME + " v" + VERSION + " is loading...");
    }
}
