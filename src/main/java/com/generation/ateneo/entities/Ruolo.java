package com.generation.ateneo.entities;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name="ruoli",
    uniqueConstraints = @UniqueConstraint(name="uk_nome_ruolo",columnNames = "nome")
)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Ruolo extends GenericEntity{

    @EqualsAndHashCode.Include
    @Id//idica che la collonna contiene la PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)//per l'auto_increment
    private Long id;

    @Column(name="nome",nullable=false,length=50)
    private String nome;

    //relazione con gli UserAccount

    @ManyToMany(mappedBy = "ruoli")
    @ToString.Exclude
    private Set<UserAccount> userAccount;

}
