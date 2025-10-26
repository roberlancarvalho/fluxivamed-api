-- V9__Create_Especialidades_Table.sql
CREATE TABLE IF NOT EXISTS especialidades (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

-- Adiciona a FK para especialidades na tabela medicos
ALTER TABLE medicos
ADD CONSTRAINT fk_medicos_especialidade FOREIGN KEY (especialidade_id) REFERENCES especialidades(id);

-- Torna a especialidade_id NOT NULL após sua criação e populamento
-- (isso será feito em uma migration de dados ou alteração separada, se necessário)
-- ALTER TABLE medicos ALTER COLUMN especialidade_id SET NOT NULL;