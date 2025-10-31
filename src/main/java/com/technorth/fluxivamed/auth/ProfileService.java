package com.technorth.fluxivamed.auth;

import com.technorth.fluxivamed.auth.dto.ProfileResponseDTO;
import com.technorth.fluxivamed.auth.dto.ProfileUpdateRequestDTO;
import com.technorth.fluxivamed.core.especialidade.Especialidade;
import com.technorth.fluxivamed.core.especialidade.EspecialidadeRepository;
import com.technorth.fluxivamed.core.medico.Medico;
import com.technorth.fluxivamed.core.medico.MedicoRepository;
import com.technorth.fluxivamed.core.plantao.Plantao;
import com.technorth.fluxivamed.core.plantao.PlantaoRepository;
import com.technorth.fluxivamed.domain.User;
import com.technorth.fluxivamed.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final MedicoRepository medicoRepository;
    private final EspecialidadeRepository especialidadeRepository;
    private final PasswordEncoder passwordEncoder;
    private final PlantaoRepository plantaoRepository;

    public ProfileService(UserRepository userRepository, MedicoRepository medicoRepository, EspecialidadeRepository especialidadeRepository, PasswordEncoder passwordEncoder, PlantaoRepository plantaoRepository
            /*, StorageService */) {
        this.userRepository = userRepository;
        this.medicoRepository = medicoRepository;
        this.especialidadeRepository = especialidadeRepository;
        this.passwordEncoder = passwordEncoder;
        this.plantaoRepository = plantaoRepository;
        // this.storageService = storageService;
    }

    @Transactional(readOnly = true)
    public ProfileResponseDTO getProfile(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Optional<Medico> medicoOpt = medicoRepository.findByUserIdWithEspecialidade(user.getId());

        String crm = medicoOpt.map(Medico::getCrm).orElse(null);
        Long especialidadeId = null;
        String especialidadeNome = null;

        if (medicoOpt.isPresent() && medicoOpt.get().getEspecialidade() != null) {
            try {
                especialidadeId = medicoOpt.get().getEspecialidade().getId();
                especialidadeNome = medicoOpt.get().getEspecialidade().getNome();
            } catch (org.hibernate.LazyInitializationException e) {
                System.err.println("LazyInitializationException no ProfileService: " + e.getMessage());
            }
        }

        return new ProfileResponseDTO(user.getId(), user.getEmail(), user.getFullName(), user.getTelefone(), crm, especialidadeId, especialidadeNome, user.getFotoUrl());
    }

    @Transactional
    public ProfileResponseDTO updateProfile(String email, ProfileUpdateRequestDTO dto) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        user.setFullName(dto.fullName());
        user.setTelefone(dto.telefone());

        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }
        User savedUser = userRepository.save(user);

        Optional<Medico> medicoOpt = medicoRepository.findByUserId(savedUser.getId());

        if (medicoOpt.isPresent() || (dto.crm() != null && !dto.crm().isBlank())) {
            Medico medico = medicoOpt.orElse(new Medico());
            medico.setUser(savedUser);
            medico.setCrm(dto.crm());
            if (dto.especialidade() != null) {
                Especialidade espGerenciada = processarEspecialidade(dto.especialidade());
                medico.setEspecialidade(espGerenciada);
            } else {
                medico.setEspecialidade(null);
            }
            medicoRepository.save(medico);
        }

        return getProfile(email);
    }

    @Transactional
    public String atualizarFotoPerfil(String email, MultipartFile file) throws IOException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode estar vazio.");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Arquivo muito grande (máx 5MB).");
        }

        // Lógica de upload simulada (substitua pelo seu S3/GCS)
        // String fotoUrl = storageService.upload(file, "profile-user-" + user.getId());
        String fotoUrl = "https://s3.bucket.fake/user_" + user.getId() + "/" + file.getOriginalFilename();

        user.setFotoUrl(fotoUrl);
        userRepository.save(user);

        return fotoUrl;
    }

    @Transactional
    public void deleteProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Optional<Medico> medicoOpt = medicoRepository.findByUserId(user.getId());

        if (medicoOpt.isPresent()) {
            Medico medico = medicoOpt.get();

            List<Plantao> plantoesAfetados = plantaoRepository.findByMedicoId(medico.getId());
            for (Plantao plantao : plantoesAfetados) {
                plantao.setMedico(null);
                plantaoRepository.save(plantao);
            }

            medicoRepository.delete(medico);
        }

        userRepository.delete(user);
    }

    private Especialidade processarEspecialidade(Especialidade especialidadeDto) {
        if (especialidadeDto == null) {
            return null;
        }

        if (especialidadeDto.getId() != null && especialidadeDto.getId() > 0) {
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
}