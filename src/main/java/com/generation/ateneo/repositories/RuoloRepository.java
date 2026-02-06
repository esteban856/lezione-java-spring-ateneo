package com.generation.ateneo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.ateneo.entities.Ruolo;

public interface RuoloRepository extends JpaRepository<Ruolo, Long>{

    Optional<Ruolo> findByNome(String nome);
}
