package io.valandur.webapi.hook;

public class HookHeader {

  private String name;

  public String getName() {
    return name;
  }

  private String value;

  public String getValue() {
    return value;
  }

  public HookHeader() {
  }

  public HookHeader(String name, String value) {
    this.name = name;
    this.value = value;
  }
}
