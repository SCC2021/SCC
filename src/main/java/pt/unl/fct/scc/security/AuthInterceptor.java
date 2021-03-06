package pt.unl.fct.scc.security;

import org.springframework.web.servlet.HandlerInterceptor;
import pt.unl.fct.scc.service.AuthService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;

public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (request.getRequestURI().split("/")[1].equals("test")) {
            return true;
        } else {
            if (request.getRequestURI().split("/").length < 3) {
                response.setStatus(404);
                return false;
            }
            if (request.getRequestURI().split("/")[2].equals("login")) return true;
            if (request.getRequestURI().split("/")[2].equals("media")) {
                if (request.getMethod().equals(HttpMethod.POST)) {
                    return true;
                }
            }
            if (request.getRequestURI().split("/")[2].equals("users")) {
                if (request.getMethod().equals(HttpMethod.POST)) {
                    return true;
                }
            }
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                response.setStatus(401);
                return false;
            }
            for (Cookie c : cookies) {

                String sessioId = c.getValue().split("\\.")[0];

                if (authService.searchSession(sessioId, c.getValue())) {
                    response.setStatus(200);
                    return true;
                }

                response.setStatus(401);
                return false;
            }
        }
        return true;
    }
}
