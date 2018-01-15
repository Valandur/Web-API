package valandur.webapi.cache.world;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.world.GeneratorType;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.misc.CachedCatalogType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CachedGeneratorType extends CachedCatalogType {

    protected Map<String, Object> settings = new HashMap<>();


    public CachedGeneratorType(GeneratorType generatorType) {
        super(generatorType);

        for (DataQuery query : generatorType.getGeneratorSettings().getKeys(true)) {
            Optional val = generatorType.getGeneratorSettings().get(query);
            if (!val.isPresent())
                continue;

            this.settings.put(query.asString("."), WebAPI.getCacheService().asCachedObject(val.get()));
        }
    }
}
