package com.generation.ateneo.DTO;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.generation.ateneo.entities.StatusStudente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

//NotNull
//NotBlank
//Email
//Min(valore)

//front end -> back end -> StudenteDTO  -> Studente -> db
@Data
public class StudenteFormDTO {

    private Long id;

    //prima di eseguire il metodo che userà questo campo verifica che il campo di questo dto 
    //segua la regola di validazione annotata sopra di esso
    //persona
    @NotBlank(message = "Nome obbligatorio") //il campo non può essere vuoto
    private String nome;

    @NotBlank(message = "Cognome obbligatorio") //il campo non può essere vuoto
    private String cognome;

    private Integer eta;

    @NotBlank(message = "campo obbligatorio")
    @Email(message = "Email non valida")
    private String email;

    //regex per controllare che contenga solo numeri e il +
    @Pattern(
            regexp = "^(\\+?[0-9]{1,3})?[-.\\s]?[0-9]{6,14}$",
            message = "Telefono non valido"
    )
    private String telefono;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "Data di nascita obbligatoria")
    //se c'è un limite di età per la persona inserita mettere una regola
    private LocalDate dataNascita;

    //regex
    @NotBlank(message = "Matricola obbligatoria")
    private String matricola;

    @NotBlank(message = "Corso di laurea obbligatorio")
    private String corsoDiLaurea;

    @NotNull(message = "anno di corso obbligatorio")
    @Min(value=1, message="Deve essere iscritto almeno al primo anno")
    private Integer annoDiCorso;
    
    @NotNull(message = "status obbligatoria")
    private StatusStudente status;
    
}
