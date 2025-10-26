CREATE TABLE pagamentos (
    id BIGSERIAL PRIMARY KEY,
    valor DECIMAL(10, 2) NOT NULL,
    data_pagamento TIMESTAMP WITH TIME ZONE,
    data_vencimento TIMESTAMP WITH TIME ZONE NOT NULL,
    medico_id BIGINT NOT NULL,
    plantao_id BIGINT,
    status VARCHAR(50) NOT NULL,
    referencia_externa VARCHAR(255),
    criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pagamentos_medico FOREIGN KEY (medico_id) REFERENCES medicos(user_id), -- CORRIGIDO AQUI
    CONSTRAINT fk_pagamentos_plantao FOREIGN KEY (plantao_id) REFERENCES plantoes(id)
);

-- Remove os defaults para que o JPA possa gerenciar
ALTER TABLE pagamentos ALTER COLUMN criado_em DROP DEFAULT;
ALTER TABLE pagamentos ALTER COLUMN atualizado_em DROP DEFAULT;