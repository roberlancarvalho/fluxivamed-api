-- src/main/resources/db/migration/V12__Drop_Redundant_Inicio_And_Fim_Columns_From_Plantoes.sql

-- Remove a coluna 'inicio' se ela existir e tiver restrições NOT NULL antigas
ALTER TABLE plantoes
DROP COLUMN IF EXISTS inicio;

-- Remove a coluna 'fim' se ela existir e tiver restrições NOT NULL antigas
ALTER TABLE plantoes
DROP COLUMN IF EXISTS fim;