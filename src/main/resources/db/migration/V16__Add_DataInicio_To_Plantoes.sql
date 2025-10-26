ALTER TABLE plantoes
ADD COLUMN data_inicio TIMESTAMP WITHOUT TIME ZONE;
-- Ou o tipo de dado que você usa (ex: DATE, TIMESTAMP WITH TIME ZONE)
-- Considere adicionar 'NULL' ou um 'DEFAULT' se a tabela já tiver dados.