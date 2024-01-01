package io.valandur.webapi.hook;

import java.util.List;

public abstract class Hook {
    protected boolean enabled;
    protected String method;
    protected String address;
    protected HookDataType dataType;
    protected List<HookHeader> headers;


    public boolean isEnabled() {
        return enabled;
    }

    public String getMethod() {
        return method;
    }

    public String getAddress() {
        return address;
    }

    public HookDataType getDataType() {
        return dataType;
    }

    public List<HookHeader> getHeaders() {
        return headers;
    }


    public Hook(boolean enabled, String method, String address, HookDataType dataType, List<HookHeader> headers) {
        this.address = address;
        this.enabled = enabled;
        this.method = method;
        this.dataType = dataType;
        this.headers = headers;
    }

    public String getDataTypeHeader() {
        return switch (dataType) {
            case JSON -> "application/json";
            case XML -> "application/xml";
        };
    }
}
