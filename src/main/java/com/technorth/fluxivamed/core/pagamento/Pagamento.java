package com.technorth.fluxivamed.core.pagamento;

import com.technorth.fluxivamed.core.medico.Medico;
import com.technorth.fluxivamed.core.plantao.Plantao; // Opcional: associar a um plantão específico

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantao_id") // Pagamento pode estar ligado a um plantão específico
    private Plantao plantao;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PagamentoStatus status;

    @Column(nullable = false)
    private LocalDateTime dataVencimento;

    private LocalDateTime dataPagamento; // Data real em que o pagamento foi efetuado

    private String referenciaExterna; // Ex: ID da transação no gateway de pagamento

    // Construtor para criação
    public Pagamento(Medico medico, Plantao plantao, BigDecimal valor, PagamentoStatus status, LocalDateTime dataVencimento) {
        this.medico = medico;
        this.plantao = plantao;
        this.valor = valor;
        this.status = status;
        this.dataVencimento = dataVencimento;
    }
}