package com.generation.ateneo.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.generation.ateneo.DTO.CorsoRecordDTO;
import com.generation.ateneo.entities.Corso;
import com.generation.ateneo.mapper.CorsoMapper;
import com.generation.ateneo.repositories.CorsoRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CorsoService  extends GenericService<Long,Corso,CorsoRepository>{

    //non dichiaro come dipendenza CorsoRepository perché c'è già ereditata dalla classe padre
    //servirà la repository del docente per creare la relazione lato Java

    //@Autowired
    private final CorsoMapper mapper;//DI tramite costruttore

    //meetodo che normalizza il codice
    private String normCodice(String codice){
        return codice == null ? null : codice.trim().toUpperCase();
    }

    //lo uso nel metodo successivo che controlla l'unicità del corso lato codice
    
    //metodo che restituisce una mappa con l'errore se ho trovato che il corso preso in input
    //ha lo stesso valore di codice di un corso salavato nel db -> controllo se qualcuno
    //sta provando a mettere un valore ripetuto per codice che unico e non ripetibile
    public Map<String,String> uniqueErrorsForCreate(CorsoRecordDTO dto){
        Map<String,String> errors = new HashMap<>();
        String codice = normCodice(dto.codice());//normalizzo il codice del record e lo salvo
        //controllo se questo dice esiste già nel db
        if(codice != null && getRepository().existsByCodice(codice)){
            errors.put("codice", "Codice già presente");
        }
        return errors;
    }

    //update 
        public Map<String,String> uniqueErrorsForUpdate(Long id,CorsoRecordDTO dto){
        Map<String,String> errors = new HashMap<>();
        String codice = normCodice(dto.codice());//normalizzo il codice del record e lo salvo
        //controllo se questo dice esiste già nel db
        if(codice != null && getRepository().existsByCodiceAndIdNot(codice, id)){
            errors.put("codice", "Codice già presente");
        }
        return errors;
    }

    //crud 
    //insert -> crud
    public boolean createFromDto(CorsoRecordDTO dto){
        //converto il record in una entity
        Corso c = mapper.toEntity(dto);

        //setto il docente del corso
        //1. so che nel record c'è id del docente
        //2. cerco il docente con quell'id e me faccio restuiture
        //3. se il docente non è null lo salco come valore della proprietà del corso
        // se c'è l'obbligo di inserimento del dicente alla creazione del corso
        //se docente è null ritorno false

        //salvataggio dell'entità
        getRepository().save(c);
        return true;
    }

    //update
    public boolean updateFromDto(Long id,CorsoRecordDTO dto){
        //dal front end ci arriva un record con i dati della form di update
        //deov convertire quei dati in una entity e poi salvarla nel db
        Corso c = getByIdOrNull(id);
        if( c == null){
            return false;
        }

        //conversione da dto a entity
        //inserisce nel corso come entity i valori del dto
        mapper.updateEntity(dto, c);

        //se c'è docente collegato nel dto
        //prendere il docente e settarlo nella proprietà del corso

        //salvo la entity
        getRepository().save(c);
        return true;
    }

    //find by id ma con il dto
    public CorsoRecordDTO getDTOById(Long id){
        Corso c = getByIdOrNull(id);//ereditato da GenericService, dal padre
        return c== null ? null : mapper.toDTO(c);
    }

    //metodo che restuisce un corso cercato per id con associate già le sue iscrizioni
    public Corso getByIdWithIscrizioni(Long id){
        //getRepository().findWithIscrizioniById(id) ritorna un Optional
        //in qiesto caso va dichiarato con .orElse(valore) il valore 
        //da ritorna se il contenuto dell'optional è null
        return getRepository().findWithIscrizioniById(id).orElse(null);
    }

    

    
}
