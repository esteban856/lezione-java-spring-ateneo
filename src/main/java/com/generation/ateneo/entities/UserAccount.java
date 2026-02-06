package com.generation.ateneo.entities;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name="user_account", uniqueConstraints = {
    @UniqueConstraint(name="uk_user_username", columnNames = "username"),
    @UniqueConstraint(name="uk_user_persona",columnNames = "persona_id")
})
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserAccount extends GenericEntity implements UserDetails{

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(nullable=false, unique = true)
    private String username;

    @Column(nullable=false)
    private String password;

    @Column(nullable = false)
    private boolean attivo;

    @Column(name="creato_il", nullable = false, updatable = false)
    private Instant creato_il;

    @Column(name="ultimo_login_il")
    private Instant ultimoLoginIl;

    @PrePersist
    private void preInsert(){
        this.creato_il=Instant.now();
    }

    //RELAZIONE 1a1 se non c'è ereditarietà ma solo associazione tra due tabelle
    @OneToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "persona_id",nullable = false,foreignKey = @ForeignKey(name="fk_user_persona"))
    @ToString.Exclude
    private Persona persona;

    //mettere la relazione con i ruoli
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name="user_role", //nome della tabella associativa
        joinColumns = @JoinColumn(name="user_id",referencedColumnName = "id",foreignKey = @ForeignKey(name="fk_user")),
        inverseJoinColumns = @JoinColumn(name="ruolo_id", foreignKey = @ForeignKey(name="fk_ruolo"))
    )
    private Set<Ruolo> ruoli = new HashSet<>(); 

    public void addRuolo(Ruolo ruolo){
        if(ruolo != null){
            ruoli.add(ruolo);
        }
    }

    public void removeRuolo(Ruolo ruolo){
        if(ruolo != null){
            ruoli.remove(ruolo);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = this.ruoli.stream()
            .map(
                ruolo -> new SimpleGrantedAuthority("ROLE_" + ruolo.getNome())
            )
            .toList();

        return authorities;
    }
}
