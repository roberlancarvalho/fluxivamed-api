-- V5__Create_plantao_candidatos_table.sql
CREATE TABLE IF NOT EXISTS plantao_candidatos (
    plantao_id BIGINT NOT NULL,
    medico_id BIGINT NOT NULL,
    PRIMARY KEY (plantao_id, medico_id),
    CONSTRAINT fk_plantao_candidatos_plantao FOREIGN KEY (plantao_id) REFERENCES plantoes (id),
    CONSTRAINT fk_plantao_candidatos_medico FOREIGN KEY (medico_id) REFERENCES medicos (user_id)
);