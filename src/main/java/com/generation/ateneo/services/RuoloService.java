package com.generation.ateneo.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.generation.ateneo.entities.Ruolo;
import com.generation.ateneo.entities.TipoRuolo;
import com.generation.ateneo.entities.UserAccount;
import com.generation.ateneo.repositories.RuoloRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RuoloService extends GenericService<Long, Ruolo, RuoloRepository>{
    
    public Optional<Ruolo> saveRuoloStudente(){
        Ruolo r = new Ruolo();
        r.setNome(TipoRuolo.STU.toString());
        log.info("Salvataggio del ruolo {}", TipoRuolo.STU.toString());
        r = getRepository().save(r);
        return Optional.of(r);
    }

    public Optional<Ruolo> findByNome(String nome){
        return getRepository().findByNome(nome);
    }

}
