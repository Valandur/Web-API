package io.valandur.webapi.security;

public class KeyPermissions {

  public final int rateLimit;

  public final Access access;

  public KeyPermissions(int rateLimit, Access access) {
    this.rateLimit = rateLimit;
    this.access = access;
  }
}
