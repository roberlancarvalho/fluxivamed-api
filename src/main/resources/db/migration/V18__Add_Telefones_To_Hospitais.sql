ALTER TABLE hospitais
ADD COLUMN telefone1 VARCHAR(20),
ADD COLUMN telefone2 VARCHAR(20);

UPDATE hospitais SET telefone1 = 'N/A' WHERE telefone1 IS NULL;

ALTER TABLE hospitais
ALTER COLUMN telefone1 SET NOT NULL;