package valandur.webapi.serialize.param;

import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.cache.player.ICachedPlayerFull;
import valandur.webapi.cache.CacheService;
import valandur.webapi.util.Util;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ext.ParamConverter;
import java.util.Optional;
import java.util.UUID;

public class PlayerParamConverter implements ParamConverter<ICachedPlayer> {

    @Override
    public ICachedPlayer fromString(String value) {
        // If we didn't request a player don't try to find one
        if (value == null)
            return null;

        if (!Util.isValidUUID(value))
            throw new BadRequestException("Invalid player/user uuid");

        CacheService srv = WebAPI.getCacheService();

        Optional<ICachedPlayerFull> optPlayer = srv.getPlayer(UUID.fromString(value));
        if (!optPlayer.isPresent())
            throw new NotFoundException("Could not find player/user with uuid " + value);
        return optPlayer.get();
    }

    @Override
    public String toString(ICachedPlayer value) {
        return value.getLink();
    }
}
