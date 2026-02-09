package com.generation.ateneo.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/*qui uso le specifiche, le regole di JPA(Java Persistence API) che sono un insieme di interfacce e 
metadati(annotazioni)
cosa fa: qui hiberntate se persona è anche una tabella dovrà creare la tabella
JPA serve a dire ad hibernate che questa classe sarà una tabella e come la tabella sarà strutturata:
- quali sono le colonne
- qual è la PK
- se ci sono FK
- ruolo JPA -> fa da mediatore tra l'applicazione/hibernate/db
- permette quindi di avere la possibilità di cambiare motore(hibernate, lo strumento che crea) senza 
dover riscrivere tutto il codice lato spring 
*/

@Entity//questa annotazione di JPa indica ad Hibernate che questa classe corrisponde ad un tabella sul db
//se non fornisco indicazioni rispetto al nome della tabella, hibernate usa il nome della classe
@Table(name = "persone") //annotazione di JPA che dice ad hibernate di dare il nome "persone" alla tabella sul db
@Data
/*per l'ereditarietà ci sono tre possibilità:
- JOINED: la tabella persone ha una relazione 1a1 con i figli, lato Java la classe Persona è una superclasse
che verrà estesa. La tabella persone sarà una tabella fisica lato db e le classi figlie saranno altrettanto 
delle tabelle fisiche -> quindi conterrà qui la PK e nelle figlie le FK
- SINGLE_TABLE: tutte classo condividono la stessa tabella nel db
JPA aggiunge bella tabella creata una colonna che fa da discriminatore(tipo_persona -> studente/docente)
uso questa strategia quando la velocità di esecuzione delle query è impattante e ho bisogno di gestione facile dei dati
- TABLE_PER_CLASS:crea tante tabelle quante sono le classi concrete lato java
ognisottovclasse ha una sua tabella lato db che contiene tutti i dati di questa classe
*/
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Persona extends GenericEntity{

    //proprietà
    //questa colonna sarà la PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//il valore di questo attributo 
    @EqualsAndHashCode.Include
    //corrisponde ad AUTO_INCREMENT
    private Long id;

    @Column(name="nome",length = 80,nullable = false)
    private String nome;

    @Column(name="cognome", length = 80, nullable = false)
    private String cognome;

    @Column(name="data_nascita")
    private LocalDate dataNascita;
   
    @EqualsAndHashCode.Include
    @Column(name="email", unique = true, nullable = false,length = 100)
    private String email;
    
    @Column(name="telefono",length = 20)
    private String telefono; 
    
    @Column(name="creata_il",nullable = false,updatable = false)
    private LocalDateTime creataIl = LocalDateTime.now();//Instant rappresenta un punto istantaneo sulla linea temporale dell'UTC
    
    @Column(name="aggiornata_il")
    private LocalDateTime aggiornataIl = LocalDateTime.now();

    // @PrePersist
    // public void preSalvataggio(){
    //     LocalDateTime now = LocalDateTime.now();
    //     this.creataIl=now;
    //     this.aggiornataIl=now;
    // }

    @PreUpdate
    public void preUpdate(){
        this.aggiornataIl = LocalDateTime.now();
    }

    //manca ancora il riferimento allo userAccount
    @OneToOne(mappedBy = "persona")
    @ToString.Exclude
    private UserAccount userAccount;
    

    //età
    @Transient 
    private Integer eta;//questa proprietà non verrà mappata nel db, esiste solo lato Java

    //normalizzare la mail -> tutta in minuscolo e senza spazi
    //private String emailNormalizzata;

    //view del nome completo
    @Transient
    private String nominativoCompleto;

    //postLoad fa si che questo metodo venga invocato dopo una insert nel db
    //cioè dopo che ho salavato una persona nella tabella persone
    //spring chiama questo  metodo così ha a disposizione l'età e il nome completo della persona
    //e posso usare questi dati nel front end
    @PostLoad
    private void postLoad(){
        this.nominativoCompleto = calcolaNominativo();
        this.eta = calcolaEta();
    }

    private Integer calcolaEta(){
        if(dataNascita == null){
            return null;
        }
        LocalDate dob = dataNascita;
        LocalDate oggi = LocalDate.now();
        if(dob.isAfter(oggi)){
            return null;
        }
        return Period.between(dob, oggi).getYears();
    }

    private String calcolaNominativo(){
        String nome = (this.nome != null)?this.nome.trim() : "";
        String cognome = (this.cognome != null)?this.cognome.trim() : "";
        return nome + " " + cognome;
    }

    public Integer getEta(){
        return calcolaEta();
    }

    public String getNominativo(){
        if(nominativoCompleto == null){
            return calcolaNominativo();
        }
        return nominativoCompleto;
    }

}
