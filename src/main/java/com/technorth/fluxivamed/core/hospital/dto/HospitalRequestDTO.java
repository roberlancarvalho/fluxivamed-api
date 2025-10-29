package com.technorth.fluxivamed.core.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HospitalRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "CNPJ é obrigatório")
        @Size(min = 14, max = 18, message = "CNPJ inválido")
        String cnpj,

        String endereco,

        @NotBlank(message = "Telefone 1 é obrigatório")
        @Size(min = 10, max = 20, message = "Telefone deve ter entre 10 e 20 caracteres")
        String telefone1,

        @Size(max = 20, message = "Telefone 2 deve ter no máximo 20 caracteres")
        String telefone2
) {
}