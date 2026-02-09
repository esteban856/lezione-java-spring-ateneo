package com.generation.ateneo.mapper;

import org.springframework.stereotype.Component;


import com.generation.ateneo.DTO.CorsoRecordDTO;
import com.generation.ateneo.entities.Corso;

@Component
public class CorsoMapper {

      //da DTO a entità
    public Corso toEntity(CorsoRecordDTO dto){
        Corso c = new Corso();
        updateEntity(dto, c);
        return c;
    }

    //aggionra l'entità con i dati del dto
    public void updateEntity(CorsoRecordDTO dto,Corso c){
        //per accedere al valore del campo di un record
        //uso i metodi get impliciti
        //che hanno come sintassi -> nomeCampo() -> cioè senza getNomeCampo
        //se la proprietù è pubblica nomeRecord.nomeProprietà
        c.setCodice(dto.codice());
        c.setCrediti(dto.crediti());
        c.setTitolo(dto.titolo());
        c.setDataInizio(dto.dataInizio());
        c.setDataFine(dto.dataFine());
        c.setDescrizione(dto.descrizione());
    }

    //da entità a dto
    public CorsoRecordDTO toDTO(Corso c){
        
        return new CorsoRecordDTO(
                    c.getId(), 
                    c.getCodice(), 
                    c.getTitolo(),
                    c.getDescrizione(), 
                    c.getCrediti(),
                    c.getDataInizio(), 
                    c.getDataFine(), 
                    (c.getDocente() != null? c.getDocente().getId() : null)
                );
  
    }
    
}
