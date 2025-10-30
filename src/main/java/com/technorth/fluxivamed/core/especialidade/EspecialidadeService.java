package com.technorth.fluxivamed.core.especialidade;

import com.technorth.fluxivamed.core.especialidade.dto.EspecialidadeRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EspecialidadeService {

    private final EspecialidadeRepository especialidadeRepository;

    public EspecialidadeService(EspecialidadeRepository especialidadeRepository) {
        this.especialidadeRepository = especialidadeRepository;
    }

    @Transactional(readOnly = true)
    public List<Especialidade> listarTodas() {
        return especialidadeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Especialidade buscarPorId(Long id) {
        return especialidadeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Especialidade não encontrada com ID: " + id));
    }

    @Transactional
    public Especialidade criarEspecialidade(EspecialidadeRequestDTO dto) {
        if (especialidadeRepository.existsByNome(dto.nome())) {
            throw new IllegalArgumentException("Especialidade '" + dto.nome() + "' já existe.");
        }
        Especialidade novaEspecialidade = new Especialidade(dto.nome());
        return especialidadeRepository.save(novaEspecialidade);
    }

    @Transactional
    public Especialidade atualizarEspecialidade(Long id, EspecialidadeRequestDTO dto) {
        Especialidade especialidade = especialidadeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Especialidade não encontrada com ID: " + id));

        Optional<Especialidade> existenteComMesmoNome = especialidadeRepository.findByNome(dto.nome());
        if (existenteComMesmoNome.isPresent() && !existenteComMesmoNome.get().getId().equals(id)) {
            throw new IllegalArgumentException("O nome '" + dto.nome() + "' já está em uso por outra especialidade.");
        }

        especialidade.setNome(dto.nome());
        return especialidadeRepository.save(especialidade);
    }

    @Transactional
    public void excluirEspecialidade(Long id) {
        if (!especialidadeRepository.existsById(id)) {
            throw new EntityNotFoundException("Especialidade não encontrada com ID: " + id);
        }
        try {
            especialidadeRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Não é possível excluir esta especialidade, pois ela está associada a médicos ou plantões.");
        }
    }

    @Transactional(readOnly = true)
    public Optional<Especialidade> buscarPorNome(String nome) {
        return especialidadeRepository.findByNome(nome);
    }

    @Transactional(readOnly = true)
    public boolean existePorNome(String nome) {
        return especialidadeRepository.existsByNome(nome);
    }
}