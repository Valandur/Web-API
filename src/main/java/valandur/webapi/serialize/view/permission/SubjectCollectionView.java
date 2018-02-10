package valandur.webapi.serialize.view.permission;

import org.spongepowered.api.service.permission.SubjectCollection;
import valandur.webapi.api.serialize.BaseView;

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
