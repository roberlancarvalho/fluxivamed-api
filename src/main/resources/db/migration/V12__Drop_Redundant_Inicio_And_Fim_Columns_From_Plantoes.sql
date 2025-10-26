-- V12__Drop_Redundant_Inicio_And_Fim_Columns_From_Plantoes.sql
ALTER TABLE plantoes
DROP COLUMN IF EXISTS inicio;

ALTER TABLE plantoes
DROP COLUMN IF EXISTS fim;