package com.generation.ateneo.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name="studenti")
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@PrimaryKeyJoinColumn(name="persona_id")
public class Studente extends Persona{
    
    @Column(name = "matricola",length = 10,nullable = false,unique = true)
    private String matricola;

    @Column(name = "corso_di_laurea",length = 120)
    private String corsoDiLaurea;//potrebbe essere una relazione con una tabella dei corsi di laurea possibili

    @Column(name="anno_di_corso")
    private Integer annoDiCorso;

    //status studente
    @Enumerated(EnumType.STRING)
    @Column(name="status",nullable = false)
    private StatusStudente status = StatusStudente.ATTIVO;

    //TODO RELAZIONE con i CORSI-> iscrizione ai corsi
    @OneToMany(mappedBy = "studente", orphanRemoval = true, cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<Iscrizione> iscrizioni = new HashSet<>();

    public void addIscrizione(Iscrizione iscrizione){
        if(iscrizione != null){
            iscrizioni.add(iscrizione);
            iscrizione.setStudente(this);
        }
    }

    public void removeIscrizione(Iscrizione iscrizione){
        if(iscrizione != null){
            iscrizioni.remove(iscrizione);
            iscrizione.setStudente(null);
        }
    }


}
