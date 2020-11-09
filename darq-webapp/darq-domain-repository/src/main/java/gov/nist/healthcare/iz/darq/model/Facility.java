package gov.nist.healthcare.iz.darq.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document
public class Facility extends FacilityDescriptor {

    Set<String> members;

    public Facility() {
    }

    public Facility(String id, String name, String description, Set<String> members) {
        super(id, name, description, members.size());
        this.members = members;
    }

    public Set<String> getMembers() {
        return members;
    }

    public void setMembers(Set<String> members) {
        this.members = members;
    }
}
