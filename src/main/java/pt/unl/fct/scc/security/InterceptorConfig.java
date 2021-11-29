package pt.unl.fct.scc.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pt.unl.fct.scc.service.AuthService;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    private final AuthService authService;

    public InterceptorConfig(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(authService));
    }
}
