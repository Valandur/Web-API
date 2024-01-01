package io.valandur.webapi.hook;

import java.util.List;

public class EventHook extends Hook {

    protected HookEventType eventType;

    public HookEventType getEventType() {
        return eventType;
    }

    public EventHook(HookEventType eventType, boolean enabled, String method, String address, HookDataType dataType, List<HookHeader> headers) {
        super(enabled, method, address, dataType, headers);

        this.eventType = eventType;
    }
}
