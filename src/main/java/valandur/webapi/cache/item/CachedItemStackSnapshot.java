package valandur.webapi.cache.item;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import valandur.webapi.cache.CachedObject;

@ApiModel("ItemStackSnapshot")
public class CachedItemStackSnapshot extends CachedObject<ItemStackSnapshot> {

    @JsonValue
    public CachedItemStack stack;


    public CachedItemStackSnapshot(ItemStackSnapshot value) {
        super(value);

        this.stack = new CachedItemStack(value.createStack());
    }

    @Override
    public ItemStackSnapshot getLive() {
        return stack.getLive().createSnapshot();
    }
}
