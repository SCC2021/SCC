package pt.unl.fct.scc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pt.unl.fct.scc.model.User;

public interface UserRepo extends MongoRepository<User, String> {

}
