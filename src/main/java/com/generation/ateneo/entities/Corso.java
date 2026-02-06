package com.generation.ateneo.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name="corsi")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true,callSuper=false)
public class Corso extends GenericEntity{
    
    @EqualsAndHashCode.Include
    @Id//idica che la collonna contiene la PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)//per l'auto_increment
    private Long id;

    @EqualsAndHashCode.Include
    @Column(name="codice_corso",nullable = false,length = 40,
        columnDefinition = "VARCHAR(40)", unique = true
    )
    private String codice;

    @Column(name="titolo", nullable = false, length = 120)
    private String titolo;

    @Column(name = "descrizione",length = 500)
    private String descrizione;

    @Column(name="data_inizio")
    private LocalDateTime dataInizio;

    @Column(name="data_fine")
    private LocalDateTime dataFine;

    @Column(name="crediti") //TODO o con Validator, e da front end
    private Integer crediti;


    //RELAZIONE come va impostata rispetto a chi ha la PROPRIETA' della relazione
    //relazione con il docente Na1
    //permette di mappare una relazione molti a uno
    //N corsi -> 1 docente
    @ManyToOne(fetch = FetchType.LAZY)
    //con fetch FetchType.LAZY indico che il caricemento del docente viene fatto solo dopo che 
    //viene eseguita una select in cui sono richiesti dati del doente non disponibili nel proxy
    //(oggetto fittizio temporaneo) cioè tendenzialmente dati diversi dall'id serve per evitare
    //di fare subito una JOIn se non necessaria
    @JoinColumn(name = "docente_id", //nome che posso dare alla colonna che hibernate crea per inserire la FK
    referencedColumnName = "persona_id",
    foreignKey = @ForeignKey(name="fk_corso_docente")//serve per dare un nome al vincolo
    )// referencedColumnName = colonna della tabella target a cui punta la FK
    //ovvero la colonna che fa da referenza
    @ToString.Exclude
    private Docente docente;

    //relazione con gli studenti NaN -> iscrizione
    @OneToMany(mappedBy = "corso", fetch = FetchType.LAZY)
    @ToString.Exclude
    Set<Iscrizione> iscrizioni = new HashSet<>();

    public void addIscrizione(Iscrizione iscrizione){
        iscrizioni.add(iscrizione);
        iscrizione.setCorso(this);
    }

    public void removeIscrizione(Iscrizione iscrizione){
        iscrizioni.remove(iscrizione);
        iscrizione.setCorso(null);
    }

}
