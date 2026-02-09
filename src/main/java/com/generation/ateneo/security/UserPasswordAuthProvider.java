package com.generation.ateneo.security;



import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.stereotype.Component;

import com.generation.ateneo.entities.UserAccount;
import com.generation.ateneo.services.UserAccountService;


//Questa classe avrà lo scopo di fornire un'autenticazione oppure lanciare un eccezione,
//security la utilizzarà per gestire il login, il metodo che implementiamo infatti fornisce 
//un BEAN Authentication valido per il ciclo della chiamata (dal frontend alla risposta del controller)
@Component
public class UserPasswordAuthProvider implements AuthenticationProvider{
    
    @Autowired
    private UserAccountService userAccountService;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //l'authentication che ci arriva come parametro non è una vera authentication ma una momentanea
        //infatti trasporta le informazioni di username e password prese dal form di login
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserAccount user = userAccountService.findUserByUsernameAndPassword(username, password);
        //Se lo user restituito è null il logino non è andato a buon fine
        if(user != null){
            //Creiamo le Authorities dell'utente, un Authority è il ruolo che ha, security li gestisce come lista
            //anche se ne abbiamo uno solo, non usa le stringhe ma oggetti formalmente di tipo GrantedAuthority
            //concretamente utilizzeremo il tipo SimpleGrantedAuthority

            //Security permette di gestire i ruoli usando il prefisso 'ROLE_'
            
            // List<SimpleGrantedAuthority> authorities = user.getRuoli().stream()
            //     .map(
            //         ruolo -> new SimpleGrantedAuthority("ROLE_" + ruolo.getNome())
            //     )
            //     .toList();
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        }
        else{
            throw new BadCredentialsException("Credenziali errate");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        //Questo metodo è utilizzato internamente da security per un check sul tipo di autenticazione
        //supportata
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    
}
