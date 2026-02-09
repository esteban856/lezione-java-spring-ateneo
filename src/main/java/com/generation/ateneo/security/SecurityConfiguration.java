package com.generation.ateneo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import com.generation.ateneo.entities.TipoRuolo;
import com.generation.ateneo.security.filters.CookieFilter;

@Configuration
public class SecurityConfiguration {
    
    @Autowired
    private UserPasswordAuthProvider userPasswordAuthProvider;

    @Autowired
    private CookieFilter cookieFilter;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    private LogoutHandler logoutHandler;


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http){
        http
            .authenticationProvider(userPasswordAuthProvider)

            //
            .addFilterBefore(cookieFilter, UsernamePasswordAuthenticationFilter.class)

            //Con questo metodo posso definire quali endpoint sono accessibili a chi
            .authorizeHttpRequests(auth -> auth
                //Richieste accessbili senza Authentication
                .requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                
                //Blocco le richieste con ruolo Studente (STU)
                .requestMatchers(
                    "/studenti/{id}/iscrizioni",
                    "/studenti/{id}/iscrizioni/{iscrizioneId}/delete",
                    "/studenti/pagina-studente",
                    "/studenti/{id}/cambia-password"
                ).hasRole(TipoRuolo.STU.toString())

                //Blocco le richieste che effettuano operazione CRUD dando accesso solo al ruolo di docente (DOC)
                .requestMatchers(HttpMethod.POST, "/studenti", "/corsi").hasRole(TipoRuolo.DOC.toString())
                .requestMatchers(HttpMethod.POST,
                    "/corsi/{id}",
                    "/studenti/{id}",
                    "/studenti/{id}/delete"
                ).hasRole(TipoRuolo.DOC.toString())
                .requestMatchers(HttpMethod.GET, 
                    "/corsi/{id}/edit",
                    "/corsi/new",
                    "/studenti/{id}/edit",
                    "/studenti/new"
                ).hasRole(TipoRuolo.DOC.toString())
                .requestMatchers(HttpMethod.GET, "/studenti/{id}","/corsi/{id}").hasAnyRole(TipoRuolo.STU.toString(), TipoRuolo.DOC.toString())
                .requestMatchers(HttpMethod.GET, "/studenti", "/corsi").permitAll() //Questo solo se fatte in GET

                //Tutte le altre richieste richiedono un Authentication
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login") //Pagina che contiene il form di login (richiesta fatta in GET)
                .loginProcessingUrl("/login") //La richiesta che deve analazzare per il login effettivo (fatta in POST)
                .successHandler(loginSuccessHandler) //Gestore di login avvenuto con successo
                .failureUrl("/login") //Dove reindirizzare in caso di login fallito
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutHandler)

                //il logout se lo aspetta in POST, per specificare di gestirlo in GET scriviamo
                .logoutRequestMatcher(PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/logout"))
                .permitAll()
            );

        return http.build();

    }
}
