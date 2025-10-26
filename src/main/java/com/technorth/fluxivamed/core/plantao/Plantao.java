package com.technorth.fluxivamed.core.plantao;

import com.technorth.fluxivamed.core.candidatura.Candidatura;
import com.technorth.fluxivamed.core.especialidade.Especialidade;
import com.technorth.fluxivamed.core.hospital.Hospital;
import com.technorth.fluxivamed.core.medico.Medico;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "plantoes")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidade_id", nullable = false)
    private Especialidade especialidade;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPlantao status;

    @CreatedDate
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @LastModifiedDate
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "plantao", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Candidatura> candidatos = new HashSet<>();

    public void adicionarCandidato(Candidatura candidatura) {
        this.candidatos.add(candidatura);
        candidatura.setPlantao(this);
    }

    public void removerCandidato(Candidatura candidatura) {
        this.candidatos.remove(candidatura);
        candidatura.setPlantao(null);
    }
}