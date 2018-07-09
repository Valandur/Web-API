package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.item.AuthorData;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedText;

public class CachedAuthorData extends CachedObject<AuthorData> {

    private CachedText author;
    @JsonValue
    public CachedText getAuthor() {
        return author;
    }


    public CachedAuthorData(AuthorData value) {
        super(value);

        this.author = new CachedText(value.author().get());
    }
}
