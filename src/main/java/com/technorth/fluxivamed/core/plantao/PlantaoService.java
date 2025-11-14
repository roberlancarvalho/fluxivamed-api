package com.technorth.fluxivamed.core.plantao;

import com.technorth.fluxivamed.core.candidatura.Candidatura;
import com.technorth.fluxivamed.core.candidatura.CandidaturaStatus;
import com.technorth.fluxivamed.core.especialidade.Especialidade;
import com.technorth.fluxivamed.core.especialidade.EspecialidadeRepository;
import com.technorth.fluxivamed.core.hospital.Hospital;
import com.technorth.fluxivamed.core.hospital.HospitalRepository;
import com.technorth.fluxivamed.core.medico.Medico;
import com.technorth.fluxivamed.core.medico.MedicoRepository;
import com.technorth.fluxivamed.core.medico.dto.MedicoResponseDTO;
import com.technorth.fluxivamed.core.notification.Notification;
import com.technorth.fluxivamed.core.notification.NotificationRepository;
import com.technorth.fluxivamed.core.plantao.dto.PlantaoRequestDTO;
import com.technorth.fluxivamed.core.plantao.dto.PlantaoResponseDTO;
import com.technorth.fluxivamed.domain.User;
import com.technorth.fluxivamed.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    private final EspecialidadeRepository especialidadeRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;

    public PlantaoService(PlantaoRepository plantaoRepository, MedicoRepository medicoRepository, UserRepository userRepository, HospitalRepository hospitalRepository, EspecialidadeRepository especialidadeRepository, SimpMessagingTemplate messagingTemplate, NotificationRepository notificationRepository) {
        this.plantaoRepository = plantaoRepository;
        this.medicoRepository = medicoRepository;
        this.userRepository = userRepository;
        this.hospitalRepository = hospitalRepository;
        this.especialidadeRepository = especialidadeRepository;
        this.messagingTemplate = messagingTemplate;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public PlantaoResponseDTO criarPlantao(PlantaoRequestDTO requestDTO) {
        Hospital hospital = hospitalRepository.findById(requestDTO.hospitalId()).orElseThrow(() -> new EntityNotFoundException("Hospital não encontrado com o ID: " + requestDTO.hospitalId()));

        Especialidade especialidadeGerenciada = processarEspecialidade(requestDTO.especialidade());

        if (requestDTO.inicio().isAfter(requestDTO.fim())) {
            throw new IllegalArgumentException("A data de início não pode ser depois da data de fim.");
        }

        Plantao novoPlantao = new Plantao();
        novoPlantao.setHospital(hospital);
        novoPlantao.setEspecialidade(especialidadeGerenciada);
        novoPlantao.setDataInicio(requestDTO.inicio());
        novoPlantao.setDataFim(requestDTO.fim());
        novoPlantao.setValor(requestDTO.valor());
        novoPlantao.setStatus(StatusPlantao.DISPONIVEL);

        Plantao plantaoSalvo = plantaoRepository.save(novoPlantao);
        return convertToDto(plantaoSalvo);
    }

    @Transactional(readOnly = true)
    public Page<PlantaoResponseDTO> findAvailable(List<StatusPlantao> statusList, Long hospitalId, LocalDate data, Pageable pageable) {
        LocalDateTime dataInicio = (data != null) ? data.atStartOfDay() : null;
        LocalDateTime dataFim = (data != null) ? data.plusDays(1).atStartOfDay() : null;

        Specification<Plantao> spec = PlantaoSpecifications.withFilters(statusList, hospitalId, dataInicio, dataFim);

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

        boolean jaCandidato = plantao.getCandidatos().stream().anyMatch(candidatura -> candidatura.getMedico().getId().equals(medico.getId()));
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

        Candidatura candidaturaAprovada = plantao.getCandidatos().stream().filter(c -> c.getMedico().getId().equals(medicoAprovado.getId()) && c.getStatus() == CandidaturaStatus.PENDENTE).findFirst().orElseThrow(() -> new IllegalStateException("Este médico não é um candidato PENDENTE para este plantão."));

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

        String mensagem = "Sua candidatura para o plantão #" + plantao.getId() + " no " + plantao.getHospital().getNome() + " foi APROVADA.";
        String link = "/dashboard/plantoes/" + plantao.getId();

        Notification notification = new Notification(medicoAprovado.getUser(), mensagem, link);
        notificationRepository.save(notification);

        messagingTemplate.convertAndSendToUser(medicoAprovado.getUser().getEmail(), "/queue/notifications", notification);

        return convertToDto(plantaoSalvo);
    }

    @Transactional(readOnly = true)
    public List<PlantaoResponseDTO> findByMedico(String medicoEmail) {
        List<Plantao> plantoes = plantaoRepository.findByMedico_User_Email(medicoEmail);
        return plantoes.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public PlantaoResponseDTO atualizarPlantao(Long plantaoId, PlantaoRequestDTO plantaoRequestDTO) {
        Plantao plantao = plantaoRepository.findById(plantaoId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plantão não encontrado com ID: " + plantaoId));

        Hospital hospital = hospitalRepository.findById(plantaoRequestDTO.hospitalId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hospital não encontrado."));

        Especialidade especialidadeGerenciada = processarEspecialidade(plantaoRequestDTO.especialidade());

        plantao.setHospital(hospital);
        plantao.setEspecialidade(especialidadeGerenciada);
        plantao.setDataInicio(plantaoRequestDTO.inicio());
        plantao.setDataFim(plantaoRequestDTO.fim());
        plantao.setValor(plantaoRequestDTO.valor());

        Plantao plantaoAtualizado = plantaoRepository.save(plantao);
        return convertToDto(plantaoAtualizado);
    }

    @Transactional
    public void excluirPlantao(Long plantaoId) {
        Plantao plantao = plantaoRepository.findById(plantaoId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plantão não encontrado com ID: " + plantaoId));

        if (plantao.getStatus() == StatusPlantao.REALIZADO) {
            throw new IllegalStateException("Não é possível excluir um plantão que já possui candidaturas, médico alocado ou já foi realizado.");
        }

        plantaoRepository.delete(plantao);
    }

    @Transactional(readOnly = true)
    public PlantaoResponseDTO getPlantaoDtoById(Long id) {
        Plantao plantao = plantaoRepository.findByIdWithCandidatos(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plantão não encontrado com ID: " + id));
        return convertToDto(plantao);
    }

    private Especialidade processarEspecialidade(Especialidade especialidadeDto) {
        if (especialidadeDto == null) {
            throw new IllegalArgumentException("Dados da especialidade são obrigatórios.");
        }

        if (especialidadeDto.getId() != null) {
            return especialidadeRepository.findById(especialidadeDto.getId()).orElseThrow(() -> new IllegalArgumentException("Especialidade não encontrada com ID: " + especialidadeDto.getId()));
        }

        if (especialidadeDto.getNome() == null || especialidadeDto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da especialidade é obrigatório para novas especialidades.");
        }

        return especialidadeRepository.findByNome(especialidadeDto.getNome()).orElseGet(() -> {
            Especialidade novaEspecialidade = new Especialidade(especialidadeDto.getNome());
            return especialidadeRepository.save(novaEspecialidade);
        });
    }

    // --- CORREÇÃO APLICADA AQUI ---
    private MedicoResponseDTO convertMedicoToDto(Medico medico) {
        if (medico == null) {
            return null;
        }

        User user = medico.getUser();
        Especialidade especialidade = medico.getEspecialidade();

        String nomeCompleto = (user != null) ? user.getFullName() : null;
        String email = (user != null) ? user.getEmail() : null;
        String telefone = (user != null) ? user.getTelefone() : null;
        Long especialidadeId = (especialidade != null) ? especialidade.getId() : null;
        String especialidadeNome = (especialidade != null) ? especialidade.getNome() : null;

        return new MedicoResponseDTO(medico.getId(), nomeCompleto, medico.getCrm(), especialidadeId, especialidadeNome, email, telefone);
    }

    private PlantaoResponseDTO convertToDto(Plantao plantao) {
        String nomeMedico = (plantao.getMedico() != null && plantao.getMedico().getUser() != null) ? plantao.getMedico().getUser().getFullName() : null;

        List<MedicoResponseDTO> candidatosDTO = plantao.getCandidatos() != null ? plantao.getCandidatos().stream().filter(c -> c.getStatus() == CandidaturaStatus.PENDENTE).map(candidatura -> convertMedicoToDto(candidatura.getMedico())).collect(Collectors.toList()) : List.of();

        Long especialidadeId = null;
        String especialidadeNome = null;
        if (plantao.getEspecialidade() != null) {
            try {
                especialidadeId = plantao.getEspecialidade().getId();
                especialidadeNome = plantao.getEspecialidade().getNome();
            } catch (org.hibernate.LazyInitializationException e) {
                // Tratamento silencioso para evitar quebras em listas
            }
        }

        return new PlantaoResponseDTO(plantao.getId(), plantao.getHospital().getId(), plantao.getHospital().getNome(), plantao.getMedico() != null ? plantao.getMedico().getId() : null, nomeMedico, especialidadeId, especialidadeNome, plantao.getDataInicio(), plantao.getDataFim(), plantao.getValor(), plantao.getStatus(), candidatosDTO);
    }
}