package com.generation.ateneo.DTO;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

//un record serve per definire classi immutabili
//con campi final di sola lettura
//lo scopo è definire oggetti che servono solo per
//trasportare dati (DTO Data Transfer Object)
//public record nomeRecord(proprietà){metodi costum/niente}

//public class CorsoRecordDTO extends Record(
public record CorsoRecordDTO(
    //estende la classe final java.lang.Record quindi ha già una classe padre
    //può però implementare interfacce
    //può contenere metodi statici e d'istanza

    //definizione dei campi del record
    //questi campi sono implicitamente FINAL(quindi non ci sono setter)
	Long id,

	@NotBlank(message = "codice obbligatorio") 
    String codice,

	@NotBlank(message = "titolo obbligatorio") 
    String titolo,

	String descrizione,

	@NotNull(message = "crediti obbligatori") 
    @Min(value = 2, message = "crediti minimi 2") 
    @Max(value = 18, message = "crediti massimi 18") 
    Integer crediti,

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull(message = "la data di inizio deve esserci") 
    LocalDateTime dataInizio,

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") 
    LocalDateTime dataFine,

	Long docenteId
    //sono generati automaticamente:
    //il costruttore con tutti i campi
    //i metodi get per ogni campo
    //i metodi equals e hashcode
    //il metodo toString
)
{
    //qui posso definire metodi addizionali se servono
    //ad esempio un metodo di validazione custom

    public static CorsoRecordDTO empty(){
        //può essere utile qundo devo mostrare una form vuota in cui poi inserire dei valori
        // rappresenta il costruttore vuoto 
        return new CorsoRecordDTO(null, null, null, null, null, null, null, null);
    }

    // come scrivo il costruttore di un record se voglio usarne uno diverso da quello canino 
    //che esiste già implicitamente
    public CorsoRecordDTO{
        codice = (codice == null)? null : codice.trim();
        titolo = (titolo == null) ? null : titolo.trim();
        descrizione = (descrizione == null) ? null : descrizione.trim();
    }

    //metodo che funge da "costruttore"
    //ma che ci permette di salvare nel record l'id  che arriva come parametro
    //crea e restituisce un nuovo record in cui ho salvato quel valore
    public CorsoRecordDTO withId(Long newId){
        return new CorsoRecordDTO(newId, codice, titolo, descrizione, crediti,
             dataInizio, dataFine,docenteId);
    }

    
}
