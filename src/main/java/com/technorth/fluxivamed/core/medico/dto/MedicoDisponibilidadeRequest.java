package com.technorth.fluxivamed.core.medico.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

public class MedicoDisponibilidadeRequest {

    @NotEmpty(message = "A lista de períodos não pode estar vazia.")
    @Valid
    private List<PeriodoRequest> periodos;

    public List<PeriodoRequest> getPeriodos() {
        return periodos;
    }

    public void setPeriodos(List<PeriodoRequest> periodos) {
        this.periodos = periodos;
    }

    public static class PeriodoRequest {
        @NotNull(message = "Data e hora de início são obrigatórias.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime inicio;

        @NotNull(message = "Data e hora de fim são obrigatórias.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime fim;

        public LocalDateTime getInicio() {
            return inicio;
        }

        public void setInicio(LocalDateTime inicio) {
            this.inicio = inicio;
        }

        public LocalDateTime getFim() {
            return fim;
        }

        public void setFim(LocalDateTime fim) {
            this.fim = fim;
        }
    }
}