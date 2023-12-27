package io.valandur.webapi.hook;

import java.util.List;

public class EventHook extends Hook {

  protected HookEventType eventType;

  public EventHook(HookEventType eventType, String address, boolean enabled, String method,
      HookDataType dataType, boolean form, List<HookHeader> headers) {
    super(address, enabled, method, dataType, form, headers);

    this.eventType = eventType;
  }
}
