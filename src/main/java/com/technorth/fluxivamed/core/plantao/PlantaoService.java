package com.technorth.fluxivamed.core.plantao;

import com.technorth.fluxivamed.core.hospital.Hospital;
import com.technorth.fluxivamed.core.hospital.HospitalRepository;
import com.technorth.fluxivamed.core.medico.Medico;
import com.technorth.fluxivamed.core.medico.MedicoRepository;
import com.technorth.fluxivamed.core.medico.dto.MedicoResponseDTO;
import com.technorth.fluxivamed.core.plantao.dto.PlantaoRequestDTO;
import com.technorth.fluxivamed.core.plantao.dto.PlantaoResponseDTO;
import com.technorth.fluxivamed.domain.User;
import com.technorth.fluxivamed.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.jpa.domain.Specification; // <-- Adicionar esta importação

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlantaoService {

    private final PlantaoRepository plantaoRepository;
    private final MedicoRepository medicoRepository;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;

    public PlantaoService(PlantaoRepository plantaoRepository, MedicoRepository medicoRepository, UserRepository userRepository, HospitalRepository hospitalRepository) {
        this.plantaoRepository = plantaoRepository;
        this.medicoRepository = medicoRepository;
        this.userRepository = userRepository;
        this.hospitalRepository = hospitalRepository;
    }

    @Transactional
    public PlantaoResponseDTO criarPlantao(PlantaoRequestDTO requestDTO) {
        Hospital hospital = hospitalRepository.findById(requestDTO.hospitalId()).orElseThrow(() -> new EntityNotFoundException("Hospital não encontrado com o ID: " + requestDTO.hospitalId()));

        if (requestDTO.inicio().isAfter(requestDTO.fim())) {
            throw new IllegalArgumentException("A data de início não pode ser depois da data de fim.");
        }

        Plantao novoPlantao = new Plantao();
        novoPlantao.setHospital(hospital);
        novoPlantao.setEspecialidade(requestDTO.especialidade());
        novoPlantao.setInicio(requestDTO.inicio());
        novoPlantao.setFim(requestDTO.fim());
        novoPlantao.setValor(requestDTO.valor());
        novoPlantao.setStatus(StatusPlantao.DISPONIVEL);

        Plantao plantaoSalvo = plantaoRepository.save(novoPlantao);
        return convertToDto(plantaoSalvo);
    }

    @Transactional(readOnly = true)
    public Page<PlantaoResponseDTO> findAvailable(Long hospitalId, LocalDate data, Pageable pageable) {
        LocalDateTime dataInicio = (data != null) ? data.atStartOfDay() : null;
        LocalDateTime dataFim = (data != null) ? data.plusDays(1).atStartOfDay() : null;

        Specification<Plantao> spec = PlantaoSpecifications.withFilters(StatusPlantao.DISPONIVEL, hospitalId, dataInicio, dataFim);

        Page<Plantao> plantoesPage = plantaoRepository.findAll(spec, pageable);

        return plantoesPage.map(this::convertToDto);
    }

    @Transactional
    public PlantaoResponseDTO candidatar(Long plantaoId, String medicoEmail) {
        User user = userRepository.findByEmail(medicoEmail).orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + medicoEmail));

        Medico medico = medicoRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("Perfil de médico não encontrado para o usuário: " + medicoEmail));

        Plantao plantao = plantaoRepository.findByIdWithCandidatos(plantaoId).orElseThrow(() -> new RuntimeException("Plantão não encontrado"));

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
        Plantao plantao = plantaoRepository.findByIdWithCandidatos(plantaoId).orElseThrow(() -> new RuntimeException("Plantão não encontrado"));

        Medico medicoAprovado = medicoRepository.findById(medicoId).orElseThrow(() -> new RuntimeException("Médico não encontrado"));

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
        return plantoes.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public PlantaoResponseDTO atualizarPlantao(Long plantaoId, PlantaoRequestDTO plantaoRequestDTO) {
        Plantao plantao = plantaoRepository.findById(plantaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plantão não encontrado com ID: " + plantaoId));

        plantao.setHospital(hospitalRepository.findById(plantaoRequestDTO.hospitalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hospital não encontrado.")));
        plantao.setEspecialidade(plantaoRequestDTO.especialidade());
        plantao.setInicio(plantaoRequestDTO.inicio());
        plantao.setFim(plantaoRequestDTO.fim());
        plantao.setValor(plantaoRequestDTO.valor());

        Plantao plantaoAtualizado = plantaoRepository.save(plantao);
        return convertToDto(plantaoAtualizado);
    }

    @Transactional
    public void excluirPlantao(Long plantaoId) {
        Plantao plantao = plantaoRepository.findById(plantaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plantão não encontrado com ID: " + plantaoId));

        if (plantao.getStatus() == StatusPlantao.AGUARDANDO_APROVACAO || plantao.getStatus() == StatusPlantao.REALIZADO) {
            throw new IllegalStateException("Não é possível excluir um plantão em andamento ou concluído.");
        }
        if (plantao.getMedico() != null) {
            throw new IllegalStateException("Não é possível excluir um plantão com médico já alocado.");
        }

        System.out.println("Excluindo plantão ID: " + plantaoId);
        plantaoRepository.delete(plantao);
    }

    @Transactional(readOnly = true)
    public PlantaoResponseDTO getPlantaoDtoById(Long id) {
        Plantao plantao = plantaoRepository.findByIdWithCandidatos(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plantão não encontrado com ID: " + id));
        return convertToDto(plantao);
    }

    private MedicoResponseDTO convertMedicoToDto(Medico medico) {
        if (medico == null) {
            return null;
        }
        return new MedicoResponseDTO(medico.getId(), medico.getCrm(), medico.getUser());
    }

    private PlantaoResponseDTO convertToDto(Plantao plantao) {
        String nomeMedico = (plantao.getMedico() != null && plantao.getMedico().getUser() != null)
                ? plantao.getMedico().getUser().getFullName() : null;

        List<MedicoResponseDTO> candidatosDTO = plantao.getCandidatos() != null ?
                plantao.getCandidatos().stream()
                        .map(this::convertMedicoToDto)
                        .collect(Collectors.toList()) :
                List.of();

        return new PlantaoResponseDTO(
                plantao.getId(),
                plantao.getHospital().getId(),
                plantao.getHospital().getNome(),
                plantao.getMedico() != null ? plantao.getMedico().getId() : null,
                nomeMedico,
                plantao.getEspecialidade(),
                plantao.getInicio(),
                plantao.getFim(),
                plantao.getValor(),
                plantao.getStatus(),
                candidatosDTO
        );
    }
}