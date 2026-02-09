package com.generation.ateneo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.ateneo.entities.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long>{
    
    @EntityGraph(attributePaths = {"ruoli", "persona"})
    Optional<UserAccount> findWithRuoloAndPersonaByUsername(String username);

    @EntityGraph(attributePaths = {"ruoli", "persona"})
    Optional<UserAccount> findWithRuoloAndPersonaById(Long id);
    Optional<UserAccount> findByPersonaId(Long personaId);
}
