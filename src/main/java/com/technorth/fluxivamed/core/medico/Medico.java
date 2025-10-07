package com.technorth.fluxivamed.core.medico;

import com.technorth.fluxivamed.core.plantao.Plantao;
import com.technorth.fluxivamed.domain.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "medicos")
public class Medico {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true, nullable = false, length = 20)
    private String crm;

    @Column(nullable = false, length = 100)
    private String especialidade;

    @ManyToMany(mappedBy = "candidatos")
    private Set<Plantao> candidaturas = new HashSet<>();
}