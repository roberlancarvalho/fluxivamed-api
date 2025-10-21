CREATE TABLE medico_disponibilidades (
    id BIGSERIAL PRIMARY KEY,
    medico_id BIGINT NOT NULL,
    inicio TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    fim TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_medico_disponibilidade
        FOREIGN KEY (medico_id)                 -- Coluna local na tabela 'medico_disponibilidades'
        REFERENCES medicos(user_id)             -- Coluna referenciada na tabela 'medicos' (agora corretamente 'user_id')
        ON DELETE CASCADE
);

CREATE INDEX idx_medico_disponibilidade_medico_id ON medico_disponibilidades(medico_id);