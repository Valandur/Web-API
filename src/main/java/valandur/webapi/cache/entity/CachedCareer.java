package valandur.webapi.cache.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.Profession;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;

@ApiModel("Career")
public class CachedCareer extends CachedObject<Career> {

    @ApiModelProperty(value = "The unique id of the career", required = true)
    public String id;

    @ApiModelProperty(value = "The name of the career", required = true)
    public String name;

    @ApiModelProperty(value = "The profession within the career", required = true)
    public CachedCatalogType<Profession> profession;


    public CachedCareer(Career value) {
        super(value);

        this.id = value.getId();
        this.name = value.getTranslation().get();
        this.profession = new CachedCatalogType<>(value.getProfession());
    }
}
