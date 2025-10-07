package com.technorth.fluxivamed.core.plantao;

import com.technorth.fluxivamed.core.medico.Medico;
import com.technorth.fluxivamed.core.medico.MedicoRepository;
import com.technorth.fluxivamed.core.plantao.dto.PlantaoResponseDTO;
import com.technorth.fluxivamed.domain.User;
import com.technorth.fluxivamed.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlantaoService {

    private final PlantaoRepository plantaoRepository;
    private final MedicoRepository medicoRepository;
    private final UserRepository userRepository;

    public PlantaoService(PlantaoRepository plantaoRepository,
                          MedicoRepository medicoRepository,
                          UserRepository userRepository) {
        this.plantaoRepository = plantaoRepository;
        this.medicoRepository = medicoRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<PlantaoResponseDTO> findAvailable(Long hospitalId, LocalDate data, Pageable pageable) {
        Page<Plantao> plantoesPage;
        final StatusPlantao status = StatusPlantao.DISPONIVEL;

        LocalDateTime dataInicio = (data != null) ? data.atStartOfDay() : null;
        LocalDateTime dataFim = (data != null) ? data.plusDays(1).atStartOfDay() : null;

        boolean hasHospital = hospitalId != null;
        boolean hasData = data != null;

        // --- LÓGICA DE DECISÃO INTELIGENTE ---
        if (hasHospital && hasData) {
            plantoesPage = plantaoRepository.findByStatusAndHospitalIdAndInicioBetween(status, hospitalId, dataInicio, dataFim, pageable);
        } else if (hasHospital) {
            plantoesPage = plantaoRepository.findByStatusAndHospitalId(status, hospitalId, pageable);
        } else if (hasData) {
            plantoesPage = plantaoRepository.findByStatusAndInicioBetween(status, dataInicio, dataFim, pageable);
        } else {
            // Se não houver filtros, chama o método mais simples
            plantoesPage = plantaoRepository.findByStatus(status, pageable);
        }

        return plantoesPage.map(this::convertToDto);
    }

    @Transactional
    public void candidatar(Long plantaoId, String medicoEmail) {
        User user = userRepository.findByEmail(medicoEmail)
                .orElseThrow(() -> new RuntimeException("Usuário com email " + medicoEmail + " não encontrado."));

        Medico medico = medicoRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Perfil de médico não encontrado para o usuário: " + medicoEmail));

        Plantao plantao = plantaoRepository.findById(plantaoId)
                .orElseThrow(() -> new RuntimeException("Plantão não encontrado com o ID: " + plantaoId));

        if (plantao.getStatus() != StatusPlantao.DISPONIVEL) {
            throw new IllegalStateException("Este plantão não está mais disponível para candidatura.");
        }

        plantao.setMedico(medico);
        plantao.setStatus(StatusPlantao.AGUARDANDO_APROVACAO);

        plantaoRepository.save(plantao);
    }

    @Transactional(readOnly = true)
    public List<PlantaoResponseDTO> findByMedico(String medicoEmail) {
        List<Plantao> plantoes = plantaoRepository.findByMedico_User_Email(medicoEmail);
        return plantoes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PlantaoResponseDTO convertToDto(Plantao plantao) {
        String nomeMedico = (plantao.getMedico() != null && plantao.getMedico().getUser() != null)
                ? plantao.getMedico().getUser().getFullName() : null;

        return new PlantaoResponseDTO(
                plantao.getId(),
                plantao.getHospital().getId(),
                plantao.getHospital().getNome(),
                plantao.getMedico() != null ? plantao.getMedico().getId() : null,
                nomeMedico,
                plantao.getInicio(),
                plantao.getFim(),
                plantao.getValor(),
                plantao.getStatus()
        );
    }
}