-- V13__Create_table_candidaturas.sql
CREATE TABLE IF NOT EXISTS candidaturas (
    id BIGSERIAL PRIMARY KEY,
    plantao_id BIGINT NOT NULL,
    medico_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDENTE',
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_candidaturas_plantao FOREIGN KEY (plantao_id) REFERENCES plantoes(id),
    CONSTRAINT fk_candidaturas_medico FOREIGN KEY (medico_id) REFERENCES medicos(user_id),
    CONSTRAINT uk_candidatura UNIQUE (plantao_id, medico_id) -- Uma candidatura por médico por plantão
);

-- Remove a default para que o JPA possa gerenciar
ALTER TABLE candidaturas ALTER COLUMN criado_em DROP DEFAULT;
ALTER TABLE candidaturas ALTER COLUMN atualizado_em DROP DEFAULT;