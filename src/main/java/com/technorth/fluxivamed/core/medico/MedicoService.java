package com.technorth.fluxivamed.core.medico;

import com.technorth.fluxivamed.core.especialidade.Especialidade;
import com.technorth.fluxivamed.core.especialidade.EspecialidadeRepository;
import com.technorth.fluxivamed.core.medico.dto.*;
import com.technorth.fluxivamed.domain.User;
import com.technorth.fluxivamed.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final DisponibilidadeRepository disponibilidadeRepository;
    private final UserRepository userRepository;
    private final EspecialidadeRepository especialidadeRepository;

    public MedicoService(MedicoRepository medicoRepository, DisponibilidadeRepository disponibilidadeRepository, UserRepository userRepository, EspecialidadeRepository especialidadeRepository) {
        this.medicoRepository = medicoRepository;
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.userRepository = userRepository;
        this.especialidadeRepository = especialidadeRepository;
    }

    @Transactional(readOnly = true)
    public List<MedicoResponseDTO> listarTodosMedicos() {
        return medicoRepository.findAll().stream().map(this::convertToMedicoResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MedicoResponseDTO getMedicoById(Long id) {
        Medico medico = medicoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Médico não encontrado com o ID: " + id));
        return convertToMedicoResponseDTO(medico);
    }

    private MedicoResponseDTO convertToMedicoResponseDTO(Medico medico) {
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

    @Transactional
    public MedicoResponseDTO atualizarMedico(Long id, MedicoRequestDTO dto) {
        Medico medico = medicoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Médico não encontrado com o ID: " + id));

        User user = medico.getUser();
        if (user == null) {
            user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));
            medico.setUser(user);
        }

        Especialidade especialidade = especialidadeRepository.findById(dto.especialidadeId()).orElseThrow(() -> new EntityNotFoundException("Especialidade não encontrada com o ID: " + dto.especialidadeId()));

        user.setFullName(dto.nomeCompleto());
        user.setEmail(dto.email());
        user.setTelefone(dto.telefone());

        medico.setCrm(dto.crm());
        medico.setEspecialidade(especialidade);

        userRepository.save(user);
        Medico medicoSalvo = medicoRepository.save(medico);

        return convertToMedicoResponseDTO(medicoSalvo);
    }

    @Transactional(readOnly = true)
    public List<MedicoDisponivelDTO> findDisponiveis(LocalDateTime inicio, LocalDateTime fim, String especialidade) {
        // TODO: Implementar esta lógica de busca
        return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    public List<PeriodoDisponibilidadeResponseDTO> getDisponibilidade(Long medicoId) {
        List<Disponibilidade> disponibilidades = disponibilidadeRepository.findByMedicoId(medicoId);
        return disponibilidades.stream().map(d -> new PeriodoDisponibilidadeResponseDTO(d.getId(), d.getInicio(), d.getFim())).collect(Collectors.toList());
    }

    @Transactional
    public void definirDisponibilidade(Long medicoId, List<MedicoDisponibilidadeRequest.PeriodoRequest> periodos) {
        Medico medico = medicoRepository.findById(medicoId).orElseThrow(() -> new RuntimeException("Médico não encontrado: " + medicoId));

        disponibilidadeRepository.deleteByMedicoId(medicoId);

        if (periodos != null && !periodos.isEmpty()) {
            List<Disponibilidade> novasDisponibilidades = periodos.stream().map(p -> new Disponibilidade(medico, p.getInicio(), p.getFim())).collect(Collectors.toList());
            disponibilidadeRepository.saveAll(novasDisponibilidades);
        }
    }

    @Transactional
    public void deletarMedico(Long id) {
        if (!medicoRepository.existsById(id)) {
            throw new EntityNotFoundException("Médico não encontrado com o ID: " + id);
        }

        disponibilidadeRepository.deleteByMedicoId(id);
        medicoRepository.deleteById(id);
        userRepository.deleteById(id);
    }

    @Transactional
    public void removerDisponibilidade(Long disponibilidadeId, Long medicoId) {
        Disponibilidade disponibilidade = disponibilidadeRepository.findById(disponibilidadeId).orElseThrow(() -> new RuntimeException("Período de disponibilidade não encontrado: " + disponibilidadeId));

        if (!disponibilidade.getMedico().getId().equals(medicoId)) {
            throw new SecurityException("Médico não autorizado a remover este período.");
        }

        disponibilidadeRepository.delete(disponibilidade);
    }
}