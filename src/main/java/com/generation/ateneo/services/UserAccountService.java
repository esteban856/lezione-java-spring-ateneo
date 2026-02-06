package com.generation.ateneo.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.generation.ateneo.entities.Persona;
import com.generation.ateneo.entities.Ruolo;
import com.generation.ateneo.entities.Studente;
import com.generation.ateneo.entities.TipoRuolo;
import com.generation.ateneo.entities.UserAccount;
import com.generation.ateneo.repositories.UserAccountRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserAccountService extends GenericService<Long, UserAccount, UserAccountRepository>{
    
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private RuoloService ruoloService;


    //Le password che abbiamo nel database sono password 'hashate' cioè non sono scritte in chiaro
    //ma sono il frutto di un algoritmo che genera una stringa cifrata e non comprensibile
    //esempio se la mia password è 'ciao' quello che salvo nel database è il risultato dell'algoritmo
    //di hashing -> $2a$13$3/6bgOAB66XV.ZE9.KmrlOQfLY6q2D5xnFex9hYSHqLpBJHIQqyWe


    //Username e password che arrivano a questo service sono in chiaro
    public UserAccount findUserByUsernameAndPassword(String username, String password){
        UserAccount user = getRepository().findWithRuoloAndPersonaByUsername(username).orElse(null);

        //Controlliamo che le password corrispondono
        if(user != null && encoder.matches(password, user.getPassword())){
            //OK - Login corretto
            return user;
        }
        else{
            //KO - Login errato
            return null;
        }
    }


    public void linkUserToPersona(Persona p, TipoRuolo ruolo){
        UserAccount user = new UserAccount();

        //Usiamo l'email della persona come username
        //Usiamo la matricola come password
        user.setUsername(p.getEmail());
        user.setAttivo(true);
        

        Ruolo r = null;
        if(ruolo == TipoRuolo.STU){
            if(p instanceof Studente s){
                user.setPassword(encoder.encode(s.getMatricola()));
            }

            // r = ruoloService.findByNome(ruolo.toString()).orElse(null);
            // if(r == null){
            //     r = new Ruolo();
            //     r.setNome(ruolo.toString());
            //     r = ruoloService.save(r);
            // }


            //Cerco un ruolo con uno specifico nome all'interno del database
            //il db mi restitisce un Optional<Ruolo> che mi da la possibilità
            //di usare il metodo .or() questo metodo viene chiamato qualora nell'Optional
            //restituito dal db non ci sia un Ruolo valido
            //il metodo .or() chiede a sua volta di passargli come parametro una funzione o metodo
            //che restituiscano un Optional<Ruolo>, noi gli passiamo un metodo che esegue prima un salvataggio
            //cosi siamo sicuri che quel ruolo esiste
            r = ruoloService.findByNome(ruolo.toString()).or(
                ruoloService::saveRuoloStudente
            )
            .get();

            user.addRuolo(r);

        }
        // else if(){ //Altri ruoli

        // }

        user.setPersona(p);
        p.setUserAccount(user);

        getRepository().save(user);
    }

    @Override
    public Optional<UserAccount> findById(Long id) {
        return getRepository().findWithRuoloAndPersonaById(id);
    }
}
