-- Adiciona a coluna de especialidade na tabela de plantões.
ALTER TABLE plantoes ADD COLUMN especialidade VARCHAR(100);

-- Define um valor padrão para os plantões que já existem no banco
-- para que a próxima alteração (NOT NULL) não falhe.
UPDATE plantoes SET especialidade = 'Clinica Geral' WHERE especialidade IS NULL;

-- Torna a coluna obrigatória para todos os novos plantões.
ALTER TABLE plantoes ALTER COLUMN especialidade SET NOT NULL;