package io.valandur.webapi.hook;

import java.util.Collection;
import java.util.List;

public class CommandHook extends Hook {

  protected Collection<String> aliases;
  protected Collection<HookParameter> params;

  public CommandHook(Collection<String> aliases, Collection<HookParameter> params, String address,
      boolean enabled, String method, HookDataType dataType, boolean form, List<HookHeader> headers) {
    super(address, enabled, method, dataType, form, headers);

    this.aliases = aliases;
    this.params = params;
  }
}
