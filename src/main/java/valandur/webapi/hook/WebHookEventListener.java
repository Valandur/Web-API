package valandur.webapi.hook;

import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;

public class WebHookEventListener implements EventListener<Event> {

    private Class<? extends Event> clazz;

    public WebHookEventListener(Class<? extends Event> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void handle(Event event) throws Exception {
        WebHooks.notifyHooks(clazz, event);
    }
}
