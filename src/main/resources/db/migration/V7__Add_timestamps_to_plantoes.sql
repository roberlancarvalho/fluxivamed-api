-- Adiciona colunas de controle de data/hora na tabela de plantões
-- para rastrear quando um registro foi criado e atualizado pela última vez.

-- A coluna 'criado_em' que o Hibernate está sentindo falta.
-- 'with time zone' é a melhor prática para timestamps.
ALTER TABLE plantoes ADD COLUMN criado_em TIMESTAMP WITH TIME ZONE;

-- Já aproveitamos para adicionar a coluna de atualização, que será necessária em breve.
ALTER TABLE plantoes ADD COLUMN atualizado_em TIMESTAMP WITH TIME ZONE;

-- Para os registros que já existem, definimos a data de criação como a data atual
-- para que a coluna possa se tornar NOT NULL sem erros.
UPDATE plantoes SET criado_em = NOW() WHERE criado_em IS NULL;
UPDATE plantoes SET atualizado_em = NOW() WHERE atualizado_em IS NULL;

-- Torna as colunas obrigatórias para futuros registros.
ALTER TABLE plantoes ALTER COLUMN criado_em SET NOT NULL;
ALTER TABLE plantoes ALTER COLUMN atualizado_em SET NOT NULL;
