package valandur.webapi.json.request.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.json.request.item.ItemStackRequest;
import valandur.webapi.json.request.misc.DamageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JsonDeserialize
public class UpdateEntityRequest {

    @JsonDeserialize
    private String world;
    public Optional<ICachedWorld> getWorld() {
        return WebAPI.getCacheService().getWorld(world);
    }

    @JsonDeserialize
    private Vector3d position;
    public Vector3d getPosition() {
        return position;
    }

    @JsonDeserialize
    private Vector3d velocity;
    public Vector3d getVelocity() {
        return velocity;
    }

    @JsonDeserialize
    private Vector3d rotation;
    public Vector3d getRotation() {
        return rotation;
    }

    @JsonDeserialize
    private Vector3d scale;
    public Vector3d getScale() {
        return scale;
    }

    @JsonDeserialize
    private DamageRequest damage;
    public DamageRequest getDamage() {
        return damage;
    }

    @JsonDeserialize
    private List<ItemStackRequest> inventory;
    public List<ItemStackSnapshot> getInventory() throws Exception {
        List<ItemStackSnapshot> res = new ArrayList<>();
        for (ItemStackRequest stack : inventory) {
            res.add(stack.getStackSnapshot());
        }
        return res;
    }
    public boolean hasInventory() {
        return inventory != null;
    }
}
