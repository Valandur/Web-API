package valandur.webapi.serialize.view.permission;

import io.swagger.annotations.ApiModel;
import org.spongepowered.api.service.permission.SubjectCollection;
import valandur.webapi.api.serialize.BaseView;

@ApiModel("SubjectCollection")
public class SubjectCollectionView extends BaseView<SubjectCollection> {

    public String getId() {
        return value.getIdentifier();
    }

    public Integer getLoadedSubjectCount() {
        return value.getLoadedSubjects().size();
    }


    public SubjectCollectionView(SubjectCollection value) {
        super(value);
    }
}
