package com.generation.ateneo.security.filters;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.generation.ateneo.entities.UserAccount;
import com.generation.ateneo.services.UserAccountService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


//I filtri possono essere più di uno (creati da noi) e con OncePerRequestFilter vengono chiamati
//una volta per ogni richiesta
//all'interno bisogna implementare obbligariamente il metodo doFilterInternal e conviene fare un override
//del metodo shuldNotFilter
@Component
@Slf4j
public class CookieFilter extends OncePerRequestFilter{
    
    @Value("${app.cookie.nome}")
    private String cookieName;

    @Autowired
    private UserAccountService userAccountService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()){
            //Se esiste già un'Authentication valida salto questo filtro e proseguo nella catena
            filterChain.doFilter(request, response);
            return;
        }


        Cookie cookie = getCookie(request.getCookies(), cookieName);
        if(cookie != null){
            //Valore cookie: IDUSER:USERNAME:SIGNITURE
            Long id = Long.parseLong(cookie.getValue().split(":")[0]);
            //Eventuale verifica di validità della SIGNITURE

            UserAccount user = userAccountService.findById(id).orElse(null);
            if(user != null){
                // List<SimpleGrantedAuthority> authorities = user.getRuoli().stream()
                //     .map(
                //         ruolo -> new SimpleGrantedAuthority("ROLE_" + ruolo.getNome())
                //     )
                //     .toList();
                
                //Crea un Authentication
                authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("utente {} autenticato tramite cookie", user.getUsername());
            }
            else{
                cookie = new Cookie(cookieName, null);
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(0);

                response.addCookie(cookie);
            }

        }


        filterChain.doFilter(request, response);
    }
    

    //Questo metodo ci permette di specificare per quale richieste specifiche questo fitlro NON deve essere
    //utilizzato. Se restituisce true non filtra
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/login") ||
               path.startsWith("/css") ||
               path.startsWith("/js");
    }


    private Cookie getCookie(Cookie[] cookies, String cookieName){
        for(Cookie c : cookies){
            if(cookieName.equals(c.getName())){
                return c;
            }
        }

        return null;
    }
}
