-- V8__Create_Medico_Disponibilidades_Table.sql
CREATE TABLE IF NOT EXISTS medico_disponibilidades (
    id BIGSERIAL PRIMARY KEY,
    medico_id BIGINT NOT NULL,
    inicio TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    fim TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_medico_disponibilidade
        FOREIGN KEY (medico_id)
        REFERENCES medicos(user_id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_medico_disponibilidade_medico_id ON medico_disponibilidades(medico_id);