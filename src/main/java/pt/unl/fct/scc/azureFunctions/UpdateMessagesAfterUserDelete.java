package pt.unl.fct.scc.azureFunctions;

import org.springframework.stereotype.Component;
import pt.unl.fct.scc.model.Message;
import pt.unl.fct.scc.model.User;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
public class UpdateMessagesAfterUserDelete implements Function<Mono<User>, Mono<Message>> {

    @Override
    public Mono<Message> apply(Mono<User> userMono) {
        return null;
    }
}
