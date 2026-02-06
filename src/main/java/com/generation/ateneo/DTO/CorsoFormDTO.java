package com.generation.ateneo.DTO;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CorsoFormDTO {

    private Long id;

    @NotBlank(message = "codice obbligatorio")
    private String codice;

    @NotBlank(message = "titolo obbligatorio")
    private String titolo;

    private String descrizione;


    @NotNull(message = "crediti obbligatori")
    @Min(value=2, message = "crediti minimi 2")
    @Max(value = 18, message = "crediti massimi 18")
    private Integer crediti;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataInizio;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataFine;

    //@NotNull(message = "Docente obbligatorio")
    private Long docenteId;

}
