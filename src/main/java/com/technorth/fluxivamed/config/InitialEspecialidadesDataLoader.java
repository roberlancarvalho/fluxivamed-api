package com.technorth.fluxivamed.config;

import com.technorth.fluxivamed.core.especialidade.Especialidade;
import com.technorth.fluxivamed.core.especialidade.EspecialidadeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitialEspecialidadesDataLoader implements CommandLineRunner {

    private final EspecialidadeRepository especialidadeRepository;

    public InitialEspecialidadesDataLoader(EspecialidadeRepository especialidadeRepository) {
        this.especialidadeRepository = especialidadeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (especialidadeRepository.count() == 0) {
            especialidadeRepository.save(new Especialidade("Clínico Geral"));
            especialidadeRepository.save(new Especialidade("Pediatra"));
            especialidadeRepository.save(new Especialidade("Cardiologista"));
            especialidadeRepository.save(new Especialidade("Dermatologista"));
            especialidadeRepository.save(new Especialidade("Ortopedista"));
            especialidadeRepository.save(new Especialidade("Neurologista"));
            System.out.println("Especialidades iniciais populadas.");
        }
    }
}