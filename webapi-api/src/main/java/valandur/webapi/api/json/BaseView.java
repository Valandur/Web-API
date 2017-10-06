package valandur.webapi.api.json;

public class BaseView<T> {

    protected T value;

    public BaseView(T value) {
        this.value = value;
    }
}
