package pt.unl.fct.scc.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pt.unl.fct.scc.model.Channel;

public interface ChannelRepo  extends MongoRepository<Channel, String> {
}
