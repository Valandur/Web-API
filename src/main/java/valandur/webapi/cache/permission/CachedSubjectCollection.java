package valandur.webapi.cache.permission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.service.permission.SubjectCollection;
import valandur.webapi.cache.CachedObject;

@ApiModel("SubjectCollection")
public class CachedSubjectCollection extends CachedObject<SubjectCollection> {

    private String id;
    @ApiModelProperty(value = "The unique id of this subject collection", required = true)
    public String getId() {
        return id;
    }

    private int loadedSubjectCount;
    @ApiModelProperty(value = "The amount of subjects currently loaded from this collection", required = true)
    public Integer getLoadedSubjectCount() {
        return loadedSubjectCount;
    }


    public CachedSubjectCollection(SubjectCollection value) {
        super(value);

        this.id = value.getIdentifier();
        this.loadedSubjectCount = value.getLoadedSubjects().size();
    }
}
