package valandur.webapi.serialize.view.permission;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.service.permission.SubjectCollection;
import valandur.webapi.serialize.BaseView;

@ApiModel("SubjectCollection")
public class SubjectCollectionView extends BaseView<SubjectCollection> {

    @ApiModelProperty(value = "The unique id of this subject collection", required = true)
    public String getId() {
        return value.getIdentifier();
    }

    /*@ApiModelProperty(value = "The amount of subjects currently loaded from this collection", required = true)
    public Integer getLoadedSubjectCount() {
        return value.getLoadedSubjects().size();
    }*/


    public SubjectCollectionView(SubjectCollection value) {
        super(value);
    }
}
