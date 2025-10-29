package com.technorth.fluxivamed.core.hospital;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Entity
@Table(name = "hospitais")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String nome;

    @NotBlank
    @Size(min = 14, max = 18)
    @Column(nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(columnDefinition = "TEXT")
    private String endereco;

    @NotBlank
    @Size(min = 10, max = 20)
    @Column(nullable = false, length = 20)
    private String telefone1;

    @Size(max = 20)
    @Column(length = 20)
    private String telefone2;
}