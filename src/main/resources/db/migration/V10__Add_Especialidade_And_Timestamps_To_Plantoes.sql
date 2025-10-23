-- Adiciona a coluna 'especialidade' que também é usada na entidade
ALTER TABLE plantoes
ADD COLUMN IF NOT EXISTS especialidade VARCHAR(255) NOT NULL DEFAULT 'Clinico Geral';

-- Adiciona as colunas de data/hora que estão faltando
-- (Seu erro mencionou 'data_fim', mas 'criado_em' e 'atualizado_em' também estão na entidade)

-- NOTA: Sua entidade Plantao.java mapeia 'fim' para 'data_fim', e 'inicio' para 'data_inicio'
-- Vamos garantir que ambas existam.
ALTER TABLE plantoes
ADD COLUMN IF NOT EXISTS data_inicio TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;
-- ^^ ATENÇÃO: Verifique se a coluna 'data_inicio' já não existe. Se sim, remova esta linha.

ALTER TABLE plantoes
ADD COLUMN IF NOT EXISTS data_fim TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;
-- ^^ Esta é a que o erro reportou como faltando.

ALTER TABLE plantoes
ADD COLUMN IF NOT EXISTS criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE plantoes
ADD COLUMN IF NOT EXISTS atualizado_em TIMESTAMP WITHOUT TIME ZONE;

-- Modifica o default da 'especialidade' após adicioná-la (opcional)
ALTER TABLE plantoes ALTER COLUMN especialidade DROP DEFAULT;
-- Modifica os defaults das datas (opcional)
ALTER TABLE plantoes ALTER COLUMN data_inicio DROP DEFAULT;
ALTER TABLE plantoes ALTER COLUMN data_fim DROP DEFAULT;
ALTER TABLE plantoes ALTER COLUMN criado_em DROP DEFAULT;