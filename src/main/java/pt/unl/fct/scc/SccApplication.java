package pt.unl.fct.scc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class SccApplication {

    public static void main(String[] args) {
        SpringApplication.run(SccApplication.class, args);
    }

}

@EnableScheduling
@Configuration
@ConditionalOnProperty(name = "scheduling.enabled", matchIfMissing = true)
class SchedulingConfig{

}
