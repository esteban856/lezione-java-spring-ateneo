package com.generation.ateneo.mapper;

import org.springframework.stereotype.Component;

import com.generation.ateneo.DTO.CorsoFormDTO;
import com.generation.ateneo.entities.Corso;

@Component
public class CorsoMapper_old {

    //da DTO a entità
    public Corso toEntity(CorsoFormDTO dto){
        Corso c = new Corso();
        updateEntity(dto, c);
        return c;
    }


    //aggionra l'entità con i dati del dto
    public void updateEntity(CorsoFormDTO dto,Corso c){
        c.setCodice(dto.getCodice());
        c.setCrediti(dto.getCrediti());
        c.setTitolo(dto.getTitolo());
        c.setDataInizio(dto.getDataInizio());
        c.setDataFine(dto.getDataFine());
        c.setDescrizione(dto.getDescrizione());
    }

    //da entità a dto
    public CorsoFormDTO toDTO(Corso c){
        CorsoFormDTO dto = new CorsoFormDTO();
        dto.setId(c.getId());
        dto.setCodice(c.getCodice());
        dto.setCrediti(c.getCrediti());
        dto.setTitolo(c.getTitolo());
        dto.setDataInizio(c.getDataInizio());
        dto.setDataFine(c.getDataFine());
        dto.setDescrizione(c.getDescrizione());
        if(c.getDocente() != null){
            dto.setDocenteId(c.getDocente().getId());
        }
        return dto;
    }

    
}
