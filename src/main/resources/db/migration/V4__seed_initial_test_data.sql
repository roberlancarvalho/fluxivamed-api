-- V4__seed_initial_test_data.sql (Versão 2 - Mais Robusta)

-- Inserindo usuários de teste sem especificar o ID. O BIGSERIAL cuidará disso.
INSERT INTO users (email, password, full_name) VALUES
('dr.house@example.com', '$2a$10$v4E3z.g4.1qKu1v.31oRz.nhzPKe4w.I.3.x.3qK6b.sB1w2g5z8C', 'Dr. Gregory House'),
('dr.grey@example.com', '$2a$10$v4E3z.g4.1qKu1v.31oRz.nhzPKe4w.I.3.x.3qK6b.sB1w2g5z8C', 'Dr. Meredith Grey'),
('escalista.chefe@example.com', '$2a$10$v4E3z.g4.1qKu1v.31oRz.nhzPKe4w.I.3.x.3qK6b.sB1w2g5z8C', 'Chefe de Escala');

-- Ligando os usuários aos seus perfis usando sub-queries para pegar os IDs corretos.
-- Isso torna o script independente da ordem de inserção ou dos IDs automáticos.
INSERT INTO user_roles (user_id, role_id) VALUES
((SELECT id from users WHERE email = 'dr.house@example.com'), (SELECT id from roles WHERE name = 'MEDICO')),
((SELECT id from users WHERE email = 'dr.grey@example.com'), (SELECT id from roles WHERE name = 'MEDICO')),
((SELECT id from users WHERE email = 'escalista.chefe@example.com'), (SELECT id from roles WHERE name = 'ESCALISTA'));

-- Inserindo um hospital de teste, também sem o ID
INSERT INTO hospitais (nome, cnpj, endereco) VALUES
('Hospital Central de Niterói', '12.345.678/0001-99', 'Rua dos Bobos, 0');

-- Inserindo os perfis dos médicos
INSERT INTO medicos (user_id, crm, especialidade) VALUES
((SELECT id from users WHERE email = 'dr.house@example.com'), 'CRM/RJ 123456', 'Cardiologia'),
((SELECT id from users WHERE email = 'dr.grey@example.com'), 'CRM/RJ 654321', 'Ortopedia');

-- Inserindo plantões de teste, usando sub-queries para os IDs
INSERT INTO plantoes (hospital_id, medico_id, inicio, fim, valor, status) VALUES
((SELECT id from hospitais WHERE cnpj = '12.345.678/0001-99'), (SELECT id from users WHERE email = 'dr.house@example.com'), '2025-10-20 07:00:00', '2025-10-20 19:00:00', 1200.00, 'PREENCHIDO'),
((SELECT id from hospitais WHERE cnpj = '12.345.678/0001-99'), NULL, '2025-10-21 07:00:00', '2025-10-21 19:00:00', 1350.50, 'DISPONIVEL'),
((SELECT id from hospitais WHERE cnpj = '12.345.678/0001-99'), NULL, '2025-10-22 19:00:00', '2025-10-23 07:00:00', 1500.00, 'DISPONIVEL');

-- Não precisamos mais do setval, pois não estamos definindo IDs manualmente.