package pt.unl.fct.scc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pt.unl.fct.scc.model.Message;

public interface MessageRepo extends MongoRepository<Message, String> {
}
