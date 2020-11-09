package gov.nist.healthcare.iz.darq.model;

public class UserFacilityView {

    protected String id;
    protected String name;
    protected int reports;
    protected int files;

    public UserFacilityView(String id, String name, int reports, int files) {
        this.id = id;
        this.name = name;
        this.reports = reports;
        this.files = files;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReports() {
        return reports;
    }

    public void setReports(int reports) {
        this.reports = reports;
    }

    public int getFiles() {
        return files;
    }

    public void setFiles(int files) {
        this.files = files;
    }
}
