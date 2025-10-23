package com.technorth.fluxivamed.core.especialidade;

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

    @Transactional
    public Especialidade criarEspecialidade(String nome) {
        if (especialidadeRepository.existsByNome(nome)) {
            throw new IllegalArgumentException("Especialidade '" + nome + "' já existe.");
        }
        Especialidade novaEspecialidade = new Especialidade(nome);
        return especialidadeRepository.save(novaEspecialidade);
    }

    @Transactional(readOnly = true)
    public Optional<Especialidade> buscarPorNome(String nome) {
        return especialidadeRepository.findByNome(nome);
    }

    @Transactional(readOnly = true)
    public Optional<Especialidade> buscarPorId(Long id) {
        return especialidadeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean existePorNome(String nome) {
        return especialidadeRepository.existsByNome(nome);
    }
}