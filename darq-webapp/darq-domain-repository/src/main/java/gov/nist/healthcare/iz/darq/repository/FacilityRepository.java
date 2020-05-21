package gov.nist.healthcare.iz.darq.repository;

import gov.nist.healthcare.iz.darq.model.Facility;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FacilityRepository extends MongoRepository<Facility, String> {

    @Query("{ '$and' : [ { _id : ?0 } , {'members' : { $in: ?1 }} ] }")
    boolean userExistsIn(String user, String facility);
    List<Facility> findByMembersContaining(String user);
}
