package com.technorth.fluxivamed.core.medico;

import com.technorth.fluxivamed.core.especialidade.Especialidade;
import com.technorth.fluxivamed.core.medico.dto.MedicoDisponibilidadeRequest;
import com.technorth.fluxivamed.core.medico.dto.MedicoDisponivelDTO;
import com.technorth.fluxivamed.core.medico.dto.MedicoResponseDTO;
import com.technorth.fluxivamed.core.medico.dto.PeriodoDisponibilidadeResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final DisponibilidadeRepository disponibilidadeRepository;

    public MedicoService(MedicoRepository medicoRepository, DisponibilidadeRepository disponibilidadeRepository) {
        this.medicoRepository = medicoRepository;
        this.disponibilidadeRepository = disponibilidadeRepository;
    }

    @Transactional(readOnly = true)
    public List<MedicoResponseDTO> listarTodosMedicos() {
        return medicoRepository.findAll().stream()
                .map(this::convertToMedicoResponseDTO)
                .collect(Collectors.toList());
    }

    private MedicoResponseDTO convertToMedicoResponseDTO(Medico medico) {
        if (medico == null) {
            return null;
        }

        String nomeCompleto = (medico.getUser() != null) ? medico.getUser().getFullName() : null;
        String email = (medico.getUser() != null) ? medico.getUser().getEmail() : null;
        String telefone = (medico.getUser() != null) ? medico.getUser().getTelefone() : null;

        String especialidadeNome = Optional.ofNullable(medico.getEspecialidade())
                .map(Especialidade::getNome)
                .orElse(null);

        return new MedicoResponseDTO(
                medico.getId(),
                nomeCompleto,
                medico.getCrm(),
                especialidadeNome,
                email,
                telefone
        );
    }

    @Transactional(readOnly = true)
    public List<MedicoDisponivelDTO> findDisponiveis(LocalDateTime inicio, LocalDateTime fim, String especialidade) {
        // Implemente a lógica real aqui se necessário
        return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<PeriodoDisponibilidadeResponseDTO> getDisponibilidade(Long medicoId) {
        List<Disponibilidade> disponibilidades = disponibilidadeRepository.findByMedicoId(medicoId);
        System.out.println("Buscando disponibilidade REAL para Medico ID " + medicoId + ": Encontrados " + disponibilidades.size() + " períodos.");
        return disponibilidades.stream()
                .map(d -> new PeriodoDisponibilidadeResponseDTO(d.getId(), d.getInicio(), d.getFim()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void definirDisponibilidade(Long medicoId, List<MedicoDisponibilidadeRequest.PeriodoRequest> periodos) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado: " + medicoId));

        System.out.println("Deletando disponibilidades antigas para Medico ID " + medicoId);
        disponibilidadeRepository.deleteByMedicoId(medicoId);

        if (periodos != null && !periodos.isEmpty()) {
            List<Disponibilidade> novasDisponibilidades = periodos.stream()
                    .map(p -> new Disponibilidade(medico, p.getInicio(), p.getFim()))
                    .collect(Collectors.toList());
            System.out.println("Salvando " + novasDisponibilidades.size() + " novas disponibilidades para Medico ID " + medicoId);
            disponibilidadeRepository.saveAll(novasDisponibilidades);
        } else {
            System.out.println("Nenhum novo período de disponibilidade fornecido para Medico ID " + medicoId);
        }
    }

    private MedicoDisponivelDTO convertToDto(Medico medico) {
        return new MedicoDisponivelDTO(
                medico.getId(),
                medico.getUser().getFullName(),
                medico.getCrm(),
                medico.getEspecialidade()
        );
    }

    @Transactional
    public void removerDisponibilidade(Long disponibilidadeId, Long medicoId) {
        Disponibilidade disponibilidade = disponibilidadeRepository.findById(disponibilidadeId)
                .orElseThrow(() -> new RuntimeException("Período de disponibilidade não encontrado: " + disponibilidadeId));

        if (!disponibilidade.getMedico().getId().equals(medicoId)) {
            throw new SecurityException("Médico não autorizado a remover este período.");
        }

        System.out.println("Removendo disponibilidade ID " + disponibilidadeId + " para Medico ID " + medicoId);
        disponibilidadeRepository.delete(disponibilidade);
    }
}