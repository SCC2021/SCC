package pt.unl.fct.scc.security;

import org.springframework.web.servlet.HandlerInterceptor;
import pt.unl.fct.scc.service.AuthService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (request.getRequestURI().split("/")[1].equals("login")){
            return true;
        }else {
            Cookie[] cookies = request.getCookies();
            if (cookies == null){
                response.setStatus(401);
                return false;
            }
            for (Cookie c : cookies){

                String sessioId = c.getValue().split("\\.")[0];
                String userId = c.getValue().split("\\.")[1];


                if(authService.searchSession(sessioId, c.getValue())){
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
