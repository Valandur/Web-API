package io.valandur.webapi;

public interface AsyncTask {
    void cancel();
    void await() throws Exception;
}
