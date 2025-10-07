package com.technorth.fluxivamed.core.hospital;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "hospitais")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(unique = true, nullable = false, length = 18)
    private String cnpj;

    private String endereco;
}