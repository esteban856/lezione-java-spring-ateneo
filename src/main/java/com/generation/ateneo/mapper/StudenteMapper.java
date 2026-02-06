package com.generation.ateneo.mapper;

import org.springframework.stereotype.Component;
import com.generation.ateneo.DTO.StudenteFormDTO;
import com.generation.ateneo.entities.Studente;


@Component//dice a spring di creare un bean in singleton di gestirlo e salvarlo nel context
//è un bean generico, non ha semanticamente un valore come repository o service o controller
public class StudenteMapper {

    //classe che serve a contenere i metodi che permetto di fare il passaggio da dto a studente e viceversa
    

    //da DTO a studente
    public Studente toEntity(StudenteFormDTO dto){
        Studente s = new Studente();
        updateEntity(dto, s);
        return s;
    }


    public void updateEntity(StudenteFormDTO dto,Studente s){
        s.setNome(dto.getNome());
        s.setCognome(dto.getCognome());
        s.setEmail(dto.getEmail());
        s.setDataNascita(dto.getDataNascita());
        s.setTelefono(dto.getTelefono());

        s.setMatricola(dto.getMatricola());
        s.setCorsoDiLaurea(dto.getCorsoDiLaurea());
        s.setAnnoDiCorso(dto.getAnnoDiCorso());
        s.setStatus(dto.getStatus());
    }
    
    //da entità a dto
    public StudenteFormDTO toDto(Studente s){
        StudenteFormDTO dto = new StudenteFormDTO();
        dto.setNome(s.getNome());
        dto.setId(s.getId());
        dto.setCognome(s.getCognome());
        dto.setDataNascita(s.getDataNascita());
        dto.setTelefono(s.getTelefono());
        dto.setEmail(s.getEmail());
        dto.setEta(s.getEta());
        dto.setMatricola(s.getMatricola());
        dto.setCorsoDiLaurea(s.getCorsoDiLaurea());
        dto.setAnnoDiCorso(s.getAnnoDiCorso());
        dto.setStatus(s.getStatus());

        return dto;
    }




}
