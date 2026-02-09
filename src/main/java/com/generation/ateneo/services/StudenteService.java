    
package com.generation.ateneo.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.generation.ateneo.DTO.StudenteFormDTO;
import com.generation.ateneo.entities.StatusStudente;
import com.generation.ateneo.entities.Studente;
import com.generation.ateneo.entities.TipoRuolo;
import com.generation.ateneo.mapper.StudenteMapper;
import com.generation.ateneo.repositories.StudenteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudenteService extends GenericService
<Long, //TipoID
Studente, //E -> entità gestita da questo service
StudenteRepository //repository che estende JpaRepository<Entità,id>
>{
    
    private final StudenteMapper mapper;

    private final UserAccountService userAccountService;

    //Dependency Injection 
    //metodo 1 con costruttore
    // public StudenteService(StudenteMapper studenteMapper){
    //     this.mapper = studenteMapper;
    // }

    //metodo 2 con Autowired sul campo
    // @Autowired
    // private StudenteMapper mapper;

    //metodo 3 con il set
    //quando voglio modificare il valore della dipendenza a runtime
    //se ho valori diversi per quella dipendenza
    // @Autowired
    // public setMapper(StudenteMapper studenteMapper){
    //     this.mapper = studenteMapper;
    // }


    //metodi che permetteono di avere i servizi necessari per gestire uno studente(CRUD + relazioni)
    //in formato DTO


    //INSERT/CREATE con il DTO

    public boolean cambiaPassword(Long studenteId, String oldPassword, String newPassword) {
        Studente studente = getByIdOrNull(studenteId);
        if (studente == null || studente.getUserAccount() == null) return false;
        return userAccountService.cambiaPassword(studente.getUserAccount(), oldPassword, newPassword);
    }

    //metodi helper
    //normalizzazione email
    private String normEmail(String email){
        return (email == null) ? null : email.trim().toLowerCase();
    }

    //normalizzazione della matricola
    private String normMatricola(String matricola){
        return (matricola == null) ? null : matricola.trim().toLowerCase();
    }

    //check unicità email e matricola
    public Map<String,String> uniqueErrorsForCreate(StudenteFormDTO dto){
        Map<String,String> errors = new HashMap<>();

        String email = normEmail(dto.getEmail());
        String matricola = normMatricola(dto.getMatricola());

        //devo controllare che l'email non sia già presente nel db
        if(email != null && getRepository().existsByEmail(email)){
            errors.put("email","Email è già presente nel db");
        }

        if(matricola != null && getRepository().existsByMatricola(matricola)){
            errors.put("matricola", "Matricola già presente nel db");
        }
        return errors;
    }

    //mappa degli errori e chack di unicità in update
    public Map<String,String> uniqueErrorsForUpdate(Long id, StudenteFormDTO dto){
        Map<String,String> errors = new HashMap<>();

        String email = normEmail(dto.getEmail());
        String matricola = normMatricola(dto.getMatricola());
        
        if(email != null && getRepository().existsByEmailAndIdNot(email, id)){
            errors.put("email","Email già presente nel db");
        }

        if(matricola != null && getRepository().existsByMatricolaAndIdNot(matricola,id)){
            errors.put("matricola", "Matricola già presente nel db");
        }
        return errors;
    }

    //CRUD DTO

    //create
    public boolean createFromDto(StudenteFormDTO dto){
        String email = normEmail(dto.getEmail());
        dto.setEmail(email);

        dto.setMatricola(normMatricola(dto.getMatricola()));
        //save dell'entità non del dto
        Studente s = mapper.toEntity(dto);
        
        //Collego lo studente al sua account con ruolo STU
        s = getRepository().save(s);
        userAccountService.linkUserToPersona(s, TipoRuolo.STU);
        return s !=null;
    }

    //update
    public boolean updateFromDto(Long id,StudenteFormDTO dto){
        //controllo che lo studente esista
        Studente studente = getByIdOrNull(id);
        if(studente == null){
            return false;
        }

        dto.setEmail(normEmail(dto.getEmail()));
        dto.setMatricola(normMatricola(dto.getMatricola()));
        //devo aggiornare l'entità nel db
        // ma ad ora l'entità che ho a disposizione è un dto
        //quindi devo convertire il dto in una entità
        mapper.updateEntity(dto, studente);
        //ora posso fare il salvataggio
        getRepository().save(studente);
        return true;
    }

    //deleteStudente senza cancellarlo ma inserendo come valore del suo status "INATTIVO"
    public boolean delete(Long id){
        log.info("delete dello studente con id={}", id);
        //controllo se lo studente da cancellare esiste
        Studente s = getByIdOrNull(id);
        if(s == null){
            log.warn("delete fallita, lo studente con id={} non è stato trovato", id);
            return false;
        }

        s.setStatus(StatusStudente.INATTIVO);
        save(s);
        log.info("Delete dello studente con id id={} andata a buon fine" , id);
        return true;
    }

    public StudenteFormDTO getDtoById(Long id){
        Studente s = getByIdOrNull(id);
        return s== null ? null : mapper.toDto(s);
    }

    //metodi sulle relazioni

    //metodo per cercare uno studente tramite id e aver caricate già le sue iscrizioni(se ce ne sono)
    public Studente getByIdWithIscrizioni(Long id){
        return getRepository().findWithIscrizioneById(id).orElse(null);
    }

}
