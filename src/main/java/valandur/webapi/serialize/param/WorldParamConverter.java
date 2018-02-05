package valandur.webapi.serialize.param;

import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.cache.CacheService;
import valandur.webapi.util.Util;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ext.ParamConverter;
import java.util.Optional;
import java.util.UUID;

public class WorldParamConverter implements ParamConverter<ICachedWorld> {

    @Override
    public ICachedWorld fromString(String value) {
        if (!Util.isValidUUID(value))
            throw new BadRequestException("Invalid world uuid");

        CacheService srv = WebAPI.getCacheService();

        Optional<ICachedWorld> optWorld = srv.getWorld(UUID.fromString(value));
        if (!optWorld.isPresent())
            throw new NotFoundException("Could not find world with uuid " + value);
        return optWorld.get();
    }

    @Override
    public String toString(ICachedWorld value) {
        return value.getLink();
    }
}
