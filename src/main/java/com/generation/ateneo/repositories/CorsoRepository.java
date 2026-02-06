package com.generation.ateneo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.ateneo.entities.Corso;

public interface CorsoRepository  extends JpaRepository<Corso,Long>{

    Optional<Corso> findByCodice(String codice);
    Optional<Corso> findByTitolo(String titolo);

    // //per controllar el'esistenza di un record existsByProprietà
    // @Query("query") se non so come scrivere il nome correto del metodo per far scrivere in automatico la query
    //ad hibernate posso dare al metodo un nome qualunque e scrivere a mano la query
    //sopra la firma del metodo nell'attotazione @Query("query") usando JPQL 
    boolean existsByCodice(String codice);

    boolean existsByCodiceAndIdNot(String codice,Long id);

    //popola il corso (sapendo il suo id) con il suo docente e le iscrizioni
    // (delle iscrizioni lo studente associato)
    @EntityGraph(attributePaths={"docente","iscrizioni","iscrizioni.studente"})
    Optional<Corso> findWithIscrizioniById(Long id);
    
   //cerca il corso sapendo il suo id e prendendo solo il suo docente associato senza le iscrizioni
   


}
