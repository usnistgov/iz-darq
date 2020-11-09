package gov.nist.healthcare.iz.darq.access.domain;

public enum Action {
    VIEW(ResourceType.values()),
    CREATE(ResourceType.CONFIGURATION, ResourceType.REPORT_TEMPLATE),
    EDIT(ResourceType.CONFIGURATION, ResourceType.REPORT_TEMPLATE),
    CLONE(ResourceType.CONFIGURATION, ResourceType.REPORT_TEMPLATE),
    DELETE(ResourceType.values()),
    PUBLISH(ResourceType.CONFIGURATION, ResourceType.REPORT_TEMPLATE, ResourceType.REPORT),
    UPLOAD(ResourceType.ADF),
    ANALYSE(ResourceType.ADF),
    COMMENT(ResourceType.REPORT);

    public final ResourceType[] resource;

    Action(ResourceType... resource) {
        this.resource = resource;
    }

}