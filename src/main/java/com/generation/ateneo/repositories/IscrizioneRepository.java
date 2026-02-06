package com.generation.ateneo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.ateneo.entities.Iscrizione;

public interface IscrizioneRepository extends JpaRepository<Iscrizione,Long>{

    //controllo esistenza iscrizione sapendo id sello studente e del corso
    boolean existsByStudente_IdAndCorso_Id(Long idStudente,Long idCorso);

    //restituisce le iscrizioni di uno studente,sapendo il suo id e il corso associato ad ogni iscrizione
    //@EntityGraph("Iscrizione.full")
    @EntityGraph(attributePaths = {"studente","corso"})
    List<Iscrizione> findWithStudenteAndCorsoByStudente_Id(Long idStudente);

    //@EntityGraph("Iscrizione.full")
    @EntityGraph(attributePaths = {"studente","corso"})
    //restituisce le iscrizioni associate ad un corso e gli studenti iscritti
    List<Iscrizione> findWithStudenteAndCorsoByCorso_Id(Long idCorso);
    
}
