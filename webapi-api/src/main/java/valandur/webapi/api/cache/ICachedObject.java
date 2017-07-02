package valandur.webapi.api.cache;

import org.spongepowered.api.data.DataHolder;

import java.util.Optional;

public interface ICachedObject {

    Class getObjectClass();

    DataHolder getData();

    String getLink();

    Optional<?> getLive();
    boolean isExpired();
}
