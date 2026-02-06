package com.generation.ateneo.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="iscrizioni", 
       uniqueConstraints = @UniqueConstraint(
        name= "uk_studente_corso", 
        columnNames = {"studente_id","corso_id"}
       ))
@NamedEntityGraph( //definisco un nome al grafoe assegno ai nodi(rami) dei nomi che posso riutilizzare nel 
    //repository quando usarò il grafo per recuperare i dati dalle join
    name = "Iscrizione.full",
    attributeNodes = {
        @NamedAttributeNode("studente"),
        @NamedAttributeNode("corso")
    }
)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Iscrizione extends GenericEntity{
    
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="data_iscrizione", nullable=false)
    private LocalDate dataIscrizione;

    @Column(name="voto_finale")
    private Integer votoFinale;

    //la FK dello studente
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="studente_id",nullable = false,referencedColumnName = "id")
    private Studente studente;

    //la FK del corso
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="corso_id",nullable = false,referencedColumnName = "id")
    private Corso corso;

    //stato iscrizione al corso
    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private StatusIscrizione statusIscrizione = StatusIscrizione.ISCRITTO;


}
