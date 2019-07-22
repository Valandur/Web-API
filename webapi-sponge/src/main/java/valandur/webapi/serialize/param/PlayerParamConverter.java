package valandur.webapi.serialize.param;

import valandur.webapi.WebAPI;
import valandur.webapi.cache.CacheService;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.util.Util;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ext.ParamConverter;
import java.util.Optional;
import java.util.UUID;

public class PlayerParamConverter implements ParamConverter<CachedPlayer> {

    @Override
    public CachedPlayer fromString(String value) {
        // If we didn't request a player don't try to find one
        if (value == null)
            return null;

        if (!Util.isValidUUID(value))
            throw new BadRequestException("Invalid player/user uuid");

        CacheService srv = WebAPI.getCacheService();

        Optional<CachedPlayer> optPlayer = srv.getPlayer(UUID.fromString(value));
        if (!optPlayer.isPresent())
            throw new NotFoundException("Could not find player/user with uuid " + value);
        return optPlayer.get();
    }

    @Override
    public String toString(CachedPlayer value) {
        return value.getLink();
    }
}
