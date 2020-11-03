package gov.nist.healthcare.iz.darq.users.repository;

import gov.nist.healthcare.iz.darq.users.domain.UserEditToken;
import gov.nist.healthcare.iz.darq.users.domain.UserEditTokenType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserEditTokenRepository extends MongoRepository<UserEditToken, String> {
    UserEditToken findByIdAndType(String id, UserEditTokenType type);
    UserEditToken findByUserIdAndType(String id, UserEditTokenType type);
    UserEditToken findByIdAndUserIdAndType(String id, String userId, UserEditTokenType type);
    List<UserEditToken> findByUserId(String userId);


}
