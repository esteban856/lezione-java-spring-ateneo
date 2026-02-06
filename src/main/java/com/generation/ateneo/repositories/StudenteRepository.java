package com.generation.ateneo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
//la funzionalità che Spring Data JPA offre è quella di astrazione dei CRUD
/*
JpaRepository fornisce metodi già implementati:
- save(E entità) -> salva o aggiorna una entità
- saveAll(Iterabile) -> salavare o aggiornare una collezione/più entità con una operazione
- findById(ID id) restituisce l'entità con quell'id se non la trova restituisce un Optional<E>
- findaAll()  restituisce tutte le entità della tabella
- existById(ID id) controlla se esiste l'etità con quell'id
- findAllById(elenco di ID)
- delete(ID id) cancella entità sapendo l'id
- delete(E entità) cancella l'entità
- deleteAll() -> cancella tutti i dati della tabella
- deleteAll(filtro Id/collezione entità)
- count() conta le entità della tabella

- Pageable 
*/
import org.springframework.stereotype.Repository;
import com.generation.ateneo.entities.Studente;
import java.util.List;


@Repository 
public interface StudenteRepository extends JpaRepository<Studente,Long>{
    
    //1.Tokenizzazione -> smonta il nome del metodo secondo regole grammaticali fisse
    //- il prefisso: parole chiave come find, read,by,count
    //- cerca il soggetto
    //- cerca il By
    // se trova il by quello che è inserito dopo il By spring sa che sono dei parametri
    //divide il testo dopo by e cerca se nel soggetto esistono delle proprietà con il token/nome
    //del parametro dopo il by
    //find 
    //by
    //Email -> cerca nella entity -> studente se esiste una proprietà email e la isola
    //2.Property Tree -> albero 
    //usando la reflection crea un albero delle proprietà inserite come parametri dopo il by
    //3. crea la query all'inizio la crea
    //4.traduce la query astratta da JPQL al dialetto del db usato
    //JPQL -> Java Persistence Query Language (JPA) / HQL(Hibernate Query Language)
    //è un linguaggio simile a SQL ma orientato agli oggetti
    //se metto un nome sbagliato Springa Data JPA quando scansiona il nome del metodo e crea l'albero
    //della query, l'errore verrà intercettato subito rischiando di non far partire o terminare subito l'applicazione

    //And e OR -> findByEmailAndId -> where email = ? AND id = ?
    //LIKE valore -> containing/startingWith (findByNomeStartingWith(valore))  -> whee nome LIKE ?
    //IgnoreCase -> findByNomeIgnoreCase -> where UPPER(nome) = UPPER(parametro)
    // per il > e il < -> uso GreaterThan / LessThan -> findByEtaLessThan(valore -> where eta < ?
    Optional<Studente> findByEmail(String email);//ritorna o lo Studente se lo trova oppre un Optinal vuoto
    //ma evita valori null

    //metodo che cerca lo studente per matricola
    //@Query("SELECT s FROM Studente s WHERE s.matricola = :matricola")
    Optional<Studente> findByMatricola(String matricola);

     //@Query("SELECT s FROM Studente s WHERE s.id = :id AND s.email = :email")
    Optional<Studente> findByIdAndEmail(Long id,String email);

    //visualizzo lo studente con determinato id e le sue iscrizioni 
    //senza FETCH nelle join, hibernate andrebbe a svolgere e scrivere n query
    //perché normalmente separa le query per prendersi i dati delle iscrizioni
    //con FETCH invece chiediamo a hibernate di caricari l'associazione (le iscrizioni e i corsi )
    //insieme all'entità in una unica di query di join, cioè associa subito e popola le relazioni
    //solo per questa singola query
    @Query("SELECT DISTINCT s FROM Studente s LEFT JOIN FETCH s.iscrizioni i LEFT JOIN FETCH i.corso WHERE s.id = :id")
    Optional<Studente> findWithIscrizioneById(@Param("id") Long id);//passo il parametro per nominativo


    //Studente(radice)
    //  |_ iscrizioni(ramo 1)
    //  |_ corsi(ramo 2)
    //con il grafo diciamo Spring Data JPA di percorre i due rami in un unico viaggio
    //entoty graph dice ad hibernate di eseguire la query e fare una join immediata con le tabelle 
    //associate - iscrizioni e nel caso corsi
    @EntityGraph(attributePaths = {"iscrizioni","iscrizioni.corso"})
    //in attributePath passiamo un array di stringhe che corrispondo esattamente ai nomi dei campi(attributi) 
    //definiti nell'entità studente e che corrispondo alle entità associate 
    Optional<Studente> findWithIscrizioniById(Long id);


    boolean existsByEmail(String email); 
    boolean existsByMatricola(String matricola);

    //controllo sull'unicità in fase di update
    boolean existsByEmailAndIdNot(String email,Long id);
    boolean existsByMatricolaAndIdNot(String matricola,Long id);

}
