package com.generation.ateneo.services;

import java.time.LocalDate;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.generation.ateneo.entities.Corso;
import com.generation.ateneo.entities.Iscrizione;
import com.generation.ateneo.entities.Studente;
import com.generation.ateneo.repositories.CorsoRepository;
import com.generation.ateneo.repositories.IscrizioneRepository;
import com.generation.ateneo.repositories.StudenteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional//se non estendiamo GenericService
public class IscrizioneService {
    
    private final CorsoRepository corsoRepository;
    private final StudenteRepository studenteRepository;
    private final IscrizioneRepository iscrizioneRepository;


    public boolean iscrivi(Long studenteId,Long corsoId){
        log.info("iscrizione dello studenteid={} al corsoId={}",studenteId,corsoId);
        if(iscrizioneRepository.existsByStudente_IdAndCorso_Id(studenteId, corsoId)){
            log.warn("iscrizione bloccata");
            return false;
        }
        Studente s = studenteRepository.findById(studenteId).orElse(null);
        Corso c = corsoRepository.findById(corsoId).orElse(null);

        Iscrizione i = new Iscrizione();
        i.setDataIscrizione(LocalDate.now());

        s.addIscrizione(i);
        c.addIscrizione(i);

        try{

            iscrizioneRepository.save(i);
            return true;
        }catch(DataIntegrityViolationException e){ //se per caso ho due richieste contemporanee
            //potrei avere un problema sul vincolo di unicità dell'iscrizione
            log.warn("errore: vincolo di duplicazione");
            return false;
        }
    }


    public boolean rimuoviIscrizione(Long idIscrizione){

        Iscrizione i = iscrizioneRepository.findById(idIscrizione).orElse(null);

        if(i == null){
            return false;
        }

        Studente s = i.getStudente();
        Corso c = i.getCorso();

        if(s != null){ //tolgo l'associazione, la FK
            s.getIscrizioni().remove(i);
        }

        if(c != null){
            c.getIscrizioni().remove(i);
        }
        iscrizioneRepository.delete(i);
        return true;
    }

}
