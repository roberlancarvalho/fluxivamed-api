package com.technorth.fluxivamed.core.medico;

import com.technorth.fluxivamed.core.candidatura.Candidatura;
import com.technorth.fluxivamed.core.especialidade.Especialidade;
import com.technorth.fluxivamed.domain.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidade_id", nullable = true)
    private Especialidade especialidade;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Candidatura> candidaturas = new HashSet<>();

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Disponibilidade> disponibilidades = new ArrayList<>();
}