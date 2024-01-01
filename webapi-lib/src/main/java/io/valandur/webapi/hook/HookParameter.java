package io.valandur.webapi.hook;

public class HookParameter {

  public enum HookParameterType {
    STRING, BOOL, INTEGER, DOUBLE, PLAYER, WORLD, LOCATION, VECTOR3D, VECTOR3I
  }

  private String name;

  public String getName() {
    return name;
  }

  private HookParameterType type;

  public HookParameterType getType() {
    return type;
  }

  private boolean optional = false;

  public boolean isOptional() {
    return optional;
  }

  public HookParameter() {
  }

  public HookParameter(String name, HookParameterType type, boolean optional) {
    this.name = name;
    this.type = type;
    this.optional = optional;
  }
}
