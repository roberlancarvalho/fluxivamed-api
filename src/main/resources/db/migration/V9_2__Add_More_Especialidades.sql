-- V9_2__Add_More_Especialidades.sql
INSERT INTO especialidades (nome) VALUES
    ('Anestesiologia'),
    ('Cardiologia'),
    ('Cirurgia Geral'),
    ('Clínica Médica'),
    ('Dermatologista'),
    ('Ginecologia'),
    ('Neurologia'),
    ('Oncologia/Obstetrícia'),
    ('Ortopedia'),
    ('Ortopedia/Traumatologia'),
    ('Pediatria'),
    ('Radiologia')
ON CONFLICT (nome) DO NOTHING;