package com.generation.ateneo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.generation.ateneo.entities.Corso;
import com.generation.ateneo.entities.Docente;
import com.generation.ateneo.entities.Studente;

@Configuration
public class EntityContext {
    
    @Bean
    @Scope("prototype")
    public Studente studente(){
        return new Studente();
    }

    @Bean
    @Scope("prototype")
    public Docente docente(){
        return new Docente();
    }

    @Bean
    @Scope("prototype")
    public Corso corso(){
        return new Corso();
    }

    //bean per ruolo
    //bean per userAccount

}
