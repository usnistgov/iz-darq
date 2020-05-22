package gov.nist.healthcare.iz.darq.model;

import java.util.Set;

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
