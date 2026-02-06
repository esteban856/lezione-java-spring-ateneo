package com.generation.ateneo.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.generation.ateneo.entities.UserAccount;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler{

    @Value("${app.cookie.nome}")
    private String cookieName;

    @Value("${app.cookie.age}")
    private int cookieMaxAge;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        UserAccount user = (UserAccount)authentication.getPrincipal();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>)authentication.getAuthorities();
        
        //Valore cookie: IDUSER:USERNAME:SIGNITURE
        Cookie cookie = new Cookie(cookieName, user.getId() + ":" + user.getUsername() + ":SIGNITURE");
        cookie.setHttpOnly(true);   //Non accessibile da JavaScript
        cookie.setSecure(false);    //da settare true quando si utilizza HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(cookieMaxAge);

        response.addCookie(cookie);

        //TODO: Possiamo gestire eventuali redirect verso pagine o sezioni precise dopo il login
                
        response.sendRedirect("/");
    }
    
}
