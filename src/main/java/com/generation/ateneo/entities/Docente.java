package com.generation.ateneo.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity//è una tabella
@Table(name="docenti")//nome tabella
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
//@Inheritance(strategy = InheritanceType.JOINED) -> dice che le classi figlie hanno una relazione di ereditarietà
//anche lato db, cioè una 1a1 dove la PK della parent(persone) è anche la PK e FK di docenti
@PrimaryKeyJoinColumn(name="persona_id")
public class Docente extends Persona {

    @Column(name="dipartimento",length=120)
    private String dipartimento;

    @Column(name = "titolo",length = 50)
    private String titolo;

    @Column(name = "ufficio",length = 50)
    private String ufficio;

    @Column(name = "area_di_ricerca",length = 50)
    private String areaDiRicerca;

    //relazione con i corsi
    //in questa relazione non il docente ad avere la proprietà ma avrà solo un riferimento alla relazione con i corsi
    //lato 1 della relazione(che è 1aN)
    @OneToMany(mappedBy = "docente")
    @ToString.Exclude
    private Set<Corso> corsi = new HashSet<>();  

    public void addCorso(Corso corso){
        corsi.add(corso);
        corso.setDocente(this);

    }

    public void rimuoviCorso(Corso corso){
        corsi.remove(corso);
        corso.setDocente(null);
    }
}
