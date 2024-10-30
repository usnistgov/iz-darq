package gov.nist.healthcare.iz.darq.service.domain;

import java.util.List;
import java.util.Set;

public class ADFMergeJobCreateData extends CreateJobData {
    List<String> tags;
    Set<String> ids;

    public ADFMergeJobCreateData(
            String name,
            String facilityId,
            String ownerId,
            List<String> tags,
            Set<String> ids
    ) {
        super(name, facilityId, ownerId);
        this.tags = tags;
        this.ids = ids;
    }

    public Set<String> getIds() {
        return ids;
    }

    public void setIds(Set<String> ids) {
        this.ids = ids;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
