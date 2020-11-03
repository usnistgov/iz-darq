package gov.nist.healthcare.iz.darq.model;

import java.util.List;

public class HomePage {
    private String title;
    private List<HomeSection> sections;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HomeSection> getSections() {
        return sections;
    }

    public void setSections(List<HomeSection> sections) {
        this.sections = sections;
    }
}
