package io.valandur.webapi.hook;

import java.util.List;

public abstract class Hook {
  protected String address;
  protected boolean enabled;
  protected boolean form;
  protected String method;
  protected HookDataType dataType;
  protected List<HookHeader> headers;


  public String getAddress() {
    return address;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public boolean isForm() {
    return form;
  }

  public String getMethod() {
    return method;
  }

  public HookDataType getDataType() {
    return dataType;
  }

  public List<HookHeader> getHeaders() {
    return headers;
  }


  public Hook(String address, boolean enabled, String method, HookDataType dataType,
      boolean form, List<HookHeader> headers) {
    this.address = address;
    this.enabled = enabled;
    this.method = method;
    this.dataType = dataType;
    this.form = form;
    this.headers = headers;
  }

  public String getDataTypeHeader() {
    if (form) {
      return "application/x-www-form-urlencoded";
    }

    switch (dataType) {
      case JSON:
        return "application/json";
      case XML:
        return "application/xml";
      default:
        return "";
    }
  }
}
