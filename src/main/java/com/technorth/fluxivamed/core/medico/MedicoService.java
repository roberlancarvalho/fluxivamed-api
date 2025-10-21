package com.technorth.fluxivamed.core.medico;

import com.technorth.fluxivamed.core.medico.dto.MedicoDisponivelDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;

    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    @Transactional(readOnly = true)
    public List<MedicoDisponivelDTO> findDisponiveis(LocalDateTime inicio, LocalDateTime fim, String especialidade) {
        if (inicio.isAfter(fim) || inicio.isEqual(fim)) {
            throw new IllegalArgumentException("A data de início deve ser anterior à data de fim.");
        }

        List<Medico> medicosDisponiveis = medicoRepository.findMedicosDisponiveis(inicio, fim, especialidade);

        return medicosDisponiveis.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private MedicoDisponivelDTO convertToDto(Medico medico) {
        return new MedicoDisponivelDTO(
                medico.getId(),
                medico.getUser().getFullName(),
                medico.getCrm(),
                medico.getEspecialidade()
        );
    }
}
