package com.technorth.fluxivamed.core.plantao;

import com.technorth.fluxivamed.core.hospital.Hospital;
import com.technorth.fluxivamed.core.medico.Medico;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal; // <<< Importar BigDecimal
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "plantoes")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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
    private String especialidade;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime inicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDateTime fim;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPlantao status;

    @CreatedDate
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @LastModifiedDate
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "plantao_candidatos",
            joinColumns = @JoinColumn(name = "plantao_id"),
            inverseJoinColumns = @JoinColumn(name = "medico_id")
    )
    private Set<Medico> candidatos = new HashSet<>();
}