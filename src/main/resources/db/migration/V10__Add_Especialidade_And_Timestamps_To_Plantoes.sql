-- Adiciona a coluna especialidade_id à tabela plantoes (se ainda não existir)
ALTER TABLE plantoes
ADD COLUMN IF NOT EXISTS especialidade_id BIGINT;

-- Adiciona a chave estrangeira para especialidade_id
-- (Garante que a tabela especialidades exista antes, o que a V9 faz)
ALTER TABLE plantoes
ADD CONSTRAINT fk_plantoes_especialidade FOREIGN KEY (especialidade_id) REFERENCES especialidades(id);

-- ATUALIZAÇÃO CRÍTICA:
-- Preenche os plantões existentes (criados em V4) com uma especialidade padrão
-- ANTES de adicionar a restrição NOT NULL.
-- Vamos usar a 'Clínica Médica' (que o V4 insere em 'especialidades')
UPDATE plantoes
SET especialidade_id = (SELECT id FROM especialidades WHERE nome = 'Clínica Médica' LIMIT 1)
WHERE especialidade_id IS NULL;

-- Agora podemos tornar a coluna obrigatória (NOT NULL)
ALTER TABLE plantoes ALTER COLUMN especialidade_id SET NOT NULL;

-- Adiciona as colunas de controle de data/hora
ALTER TABLE plantoes
ADD COLUMN IF NOT EXISTS criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE plantoes
ADD COLUMN IF NOT EXISTS atualizado_em TIMESTAMP WITH TIME ZONE;

-- Atualiza colunas de timestamp para registros existentes
UPDATE plantoes SET atualizado_em = NOW() WHERE atualizado_em IS NULL;

-- Torna atualizado_em NOT NULL
ALTER TABLE plantoes ALTER COLUMN atualizado_em SET NOT NULL;

-- Remove a default para que o JPA possa gerenciar
ALTER TABLE plantoes ALTER COLUMN criado_em DROP DEFAULT;