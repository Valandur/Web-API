package valandur.webapi.cache.player;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.cache.CacheConfig;
import valandur.webapi.cache.entity.CachedEntity;

import java.util.*;

public class CachedPlayer extends CachedEntity {

    private String name;
    public String getName() {
        return name;
    }

    private String address;
    public String getAddress() {
        return address;
    }

    private int latency;
    public int getLatency() {
        return latency;
    }

    private ItemStack helmet;
    public ItemStack getHelmet() {
        return helmet;
    }

    private ItemStack chestplate;
    public ItemStack getChestplate() {
        return chestplate;
    }

    private ItemStack leggings;
    public ItemStack getLeggings() {
        return leggings;
    }

    private ItemStack boots;
    public ItemStack getBoots() {
        return boots;
    }


    public CachedPlayer(Player player) {
        super(player);

        this.name = player.getName();

        this.address = player.getConnection().getAddress().toString();
        this.latency = player.getConnection().getLatency();

        this.helmet = player.getHelmet().map(ItemStack::copy).orElse(null);
        this.chestplate = player.getChestplate().map(ItemStack::copy).orElse(null);
        this.leggings = player.getLeggings().map(ItemStack::copy).orElse(null);
        this.boots = player.getBoots().map(ItemStack::copy).orElse(null);
    }

    @Override
    public int getCacheDuration() {
        return CacheConfig.durationPlayer;
    }
    @Override
    public Optional<?> getLive() {
        return Sponge.getServer().getPlayer(uuid);
    }

    @Override
    public String getLink() {
        return "/api/player/" + uuid;
    }
}
