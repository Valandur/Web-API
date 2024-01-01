package io.valandur.webapi.hook;

import java.util.Collection;
import java.util.List;

public class CommandHook extends Hook {

    protected Collection<String> aliases;
    protected Collection<HookParameter> params;

    public CommandHook(Collection<String> aliases, Collection<HookParameter> params, boolean enabled, String method,
                       String address, HookDataType dataType, List<HookHeader> headers) {
        super(enabled, method, address, dataType, headers);

        this.aliases = aliases;
        this.params = params;
    }
}
