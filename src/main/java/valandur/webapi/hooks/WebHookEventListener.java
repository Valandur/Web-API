package valandur.webapi.hooks;

import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import valandur.webapi.json.JsonConverter;

public class WebHookEventListener implements EventListener<Event> {

    private Class<? extends Event> clazz;

    public WebHookEventListener(Class<? extends Event> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void handle(Event event) throws Exception {
        WebHooks.notifyHooks(clazz, JsonConverter.toString(event));
    }
}
