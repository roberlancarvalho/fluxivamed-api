package com.technorth.fluxivamed.core.medico;

import com.technorth.fluxivamed.domain.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
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

}