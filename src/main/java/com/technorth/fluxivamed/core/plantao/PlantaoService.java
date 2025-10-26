package com.technorth.fluxivamed.core.plantao;

import com.technorth.fluxivamed.core.especialidade.Especialidade;
import com.technorth.fluxivamed.core.hospital.Hospital;
import com.technorth.fluxivamed.core.hospital.HospitalRepository;
import com.technorth.fluxivamed.core.medico.Medico;
import com.technorth.fluxivamed.core.medico.MedicoRepository;
import com.technorth.fluxivamed.core.medico.dto.MedicoResponseDTO;
import com.technorth.fluxivamed.core.plantao.dto.PlantaoRequestDTO;
import com.technorth.fluxivamed.core.plantao.dto.PlantaoResponseDTO;
import com.technorth.fluxivamed.domain.User;
import com.technorth.fluxivamed.repository.UserRepository;
import com.technorth.fluxivamed.core.candidatura.Candidatura;
import com.technorth.fluxivamed.core.candidatura.CandidaturaStatus;
// Não precisamos mais do import para Especialidade aqui, pois Medico.especialidade é String
// import com.technorth.fluxivamed.core.especialidade.Especialidade;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

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
        Hospital hospital = hospitalRepository.findById(requestDTO.hospitalId())
                .orElseThrow(() -> new EntityNotFoundException("Hospital não encontrado com o ID: " + requestDTO.hospitalId()));

        if (requestDTO.inicio().isAfter(requestDTO.fim())) {
            throw new IllegalArgumentException("A data de início não pode ser depois da data de fim.");
        }

        Plantao novoPlantao = new Plantao();
        novoPlantao.setHospital(hospital);
        novoPlantao.setEspecialidade(requestDTO.especialidade());
        novoPlantao.setDataInicio(requestDTO.inicio());
        novoPlantao.setDataFim(requestDTO.fim());
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

        Medico medico = medicoRepository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("Perfil de médico não encontrado para o usuário: " + medicoEmail));

        Plantao plantao = plantaoRepository.findByIdWithCandidatos(plantaoId).orElseThrow(() -> new RuntimeException("Plantão não encontrado"));

        if (plantao.getStatus() != StatusPlantao.DISPONIVEL && plantao.getStatus() != StatusPlantao.AGUARDANDO_APROVACAO) {
            throw new IllegalStateException("Só é possível se candidatar a plantões com status DISPONIVEL ou AGUARDANDO_APROVACAO.");
        }

        boolean jaCandidato = plantao.getCandidatos().stream()
                .anyMatch(candidatura -> candidatura.getMedico().getId().equals(medico.getId()));
        if (jaCandidato) {
            throw new IllegalStateException("Médico já se candidatou para este plantão.");
        }

        Candidatura novaCandidatura = new Candidatura(medico, plantao, CandidaturaStatus.PENDENTE);
        plantao.adicionarCandidato(novaCandidatura);

        if (plantao.getStatus() == StatusPlantao.DISPONIVEL) {
            plantao.setStatus(StatusPlantao.AGUARDANDO_APROVACAO);
        }

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

        Candidatura candidaturaAprovada = plantao.getCandidatos().stream()
                .filter(c -> c.getMedico().getId().equals(medicoAprovado.getId()) && c.getStatus() == CandidaturaStatus.PENDENTE)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Este médico não é um candidato PENDENTE para este plantão."));

        plantao.setMedico(medicoAprovado);
        plantao.setStatus(StatusPlantao.PREENCHIDO);

        plantao.getCandidatos().forEach(c -> {
            if (c.getId().equals(candidaturaAprovada.getId())) {
                c.setStatus(CandidaturaStatus.APROVADA);
            } else if (c.getStatus() == CandidaturaStatus.PENDENTE) {
                c.setStatus(CandidaturaStatus.REJEITADA);
            }
        });

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
        plantao.setDataInicio(plantaoRequestDTO.inicio());
        plantao.setDataFim(plantaoRequestDTO.fim());
        plantao.setValor(plantaoRequestDTO.valor());

        Plantao plantaoAtualizado = plantaoRepository.save(plantao);
        return convertToDto(plantaoAtualizado);
    }

    @Transactional
    public void excluirPlantao(Long plantaoId) {
        Plantao plantao = plantaoRepository.findById(plantaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plantão não encontrado com ID: " + plantaoId));

        if (plantao.getStatus() == StatusPlantao.AGUARDANDO_APROVACAO || plantao.getStatus() == StatusPlantao.PREENCHIDO || plantao.getStatus() == StatusPlantao.REALIZADO) {
            throw new IllegalStateException("Não é possível excluir um plantão que já possui candidaturas, médico alocado ou já foi realizado.");
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

        String nomeCompleto = (medico.getUser() != null) ? medico.getUser().getFullName() : null;
        String email = (medico.getUser() != null) ? medico.getUser().getEmail() : null;
        String telefone = (medico.getUser() != null) ? medico.getUser().getTelefone() : null;

        String especialidadeNome = Optional.ofNullable(medico.getEspecialidade())
                .map(Especialidade::getNome)
                .orElse(null);

        return new MedicoResponseDTO(
                medico.getId(),
                medico.getCrm(),
                nomeCompleto,
                email,
                telefone,
                especialidadeNome
        );
    }

    private PlantaoResponseDTO convertToDto(Plantao plantao) {
        String nomeMedico = (plantao.getMedico() != null && plantao.getMedico().getUser() != null)
                ? plantao.getMedico().getUser().getFullName() : null;

        List<MedicoResponseDTO> candidatosDTO = plantao.getCandidatos() != null ?
                plantao.getCandidatos().stream()
                        .filter(c -> c.getStatus() == CandidaturaStatus.PENDENTE)
                        .map(candidatura -> convertMedicoToDto(candidatura.getMedico()))
                        .collect(Collectors.toList()) :
                List.of();

        return new PlantaoResponseDTO(
                plantao.getId(),
                plantao.getHospital().getId(),
                plantao.getHospital().getNome(),
                plantao.getMedico() != null ? plantao.getMedico().getId() : null,
                nomeMedico,
                plantao.getEspecialidade(),
                plantao.getDataInicio(),
                plantao.getDataFim(),
                plantao.getValor(),
                plantao.getStatus(),
                candidatosDTO
        );
    }
}