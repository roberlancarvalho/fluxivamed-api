-- V9_1__Seed_Especialidades.sql
INSERT INTO especialidades (nome) VALUES
    ('Clínica Médica'),
    ('Cardiologia'),
    ('Pediatria'),
    ('Cirurgia Geral'),
    ('Anestesiologia'),
    ('Radiologia')
ON CONFLICT (nome) DO NOTHING;