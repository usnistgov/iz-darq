package gov.nist.healthcare.iz.darq.service.exception;


public class NotFoundException extends Exception {
    private final String resourceType;
    private final String id;

    public NotFoundException(String resourceType, String id) {
        super("Resource " + resourceType + " with ID " + id + " not found");
        this.resourceType = resourceType;
        this.id = id;
    }

    public NotFoundException(String message) {
        super(message);
        this.resourceType = null;
        this.id = null;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getId() {
        return id;
    }
}
