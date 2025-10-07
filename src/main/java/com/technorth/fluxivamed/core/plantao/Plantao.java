package com.technorth.fluxivamed.core.plantao;

import com.technorth.fluxivamed.core.hospital.Hospital;
import com.technorth.fluxivamed.core.medico.Medico;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "plantoes")
public class Plantao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column(nullable = false)
    private LocalDateTime fim;

    @Column(nullable = false)
    private Double valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPlantao status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "plantao_candidatos",
            joinColumns = @JoinColumn(name = "plantao_id"),
            inverseJoinColumns = @JoinColumn(name = "medico_id")
    )
    private Set<Medico> candidatos = new HashSet<>();
}