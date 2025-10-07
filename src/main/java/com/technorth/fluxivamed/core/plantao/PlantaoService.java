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
        LocalDateTime dataInicio = (data != null) ? data.atStartOfDay() : null;
        LocalDateTime dataFim = (data != null) ? data.plusDays(1).atStartOfDay() : null;

        Page<Plantao> plantoesPage = plantaoRepository.findAvailableWithFilters(
                StatusPlantao.DISPONIVEL, hospitalId, dataInicio, dataFim, pageable);

        return plantoesPage.map(this::convertToDto);
    }

    @Transactional
    public PlantaoResponseDTO candidatar(Long plantaoId, String medicoEmail) {
        User user = userRepository.findByEmail(medicoEmail)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + medicoEmail));

        Medico medico = medicoRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Perfil de médico não encontrado para o usuário: " + medicoEmail));

        Plantao plantao = plantaoRepository.findByIdWithCandidatos(plantaoId)
                .orElseThrow(() -> new RuntimeException("Plantão não encontrado"));

        if (plantao.getStatus() != StatusPlantao.DISPONIVEL) {
            throw new IllegalStateException("Só é possível se candidatar a plantões com status DISPONIVEL");
        }

        plantao.getCandidatos().add(medico);
        plantao.setStatus(StatusPlantao.AGUARDANDO_APROVACAO);

        Plantao plantaoSalvo = plantaoRepository.save(plantao);
        return convertToDto(plantaoSalvo);
    }

    @Transactional
    public PlantaoResponseDTO aprovarCandidatura(Long plantaoId, Long medicoId) {
        Plantao plantao = plantaoRepository.findByIdWithCandidatos(plantaoId)
                .orElseThrow(() -> new RuntimeException("Plantão não encontrado"));

        Medico medicoAprovado = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        if (plantao.getStatus() != StatusPlantao.AGUARDANDO_APROVACAO) {
            throw new IllegalStateException("Plantão não está aguardando aprovação.");
        }

        if (!plantao.getCandidatos().contains(medicoAprovado)) {
            throw new IllegalStateException("Este médico não é um candidato para este plantão.");
        }

        plantao.setMedico(medicoAprovado);
        plantao.setStatus(StatusPlantao.PREENCHIDO);
        plantao.getCandidatos().clear();

        Plantao plantaoSalvo = plantaoRepository.save(plantao);
        return convertToDto(plantaoSalvo);
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