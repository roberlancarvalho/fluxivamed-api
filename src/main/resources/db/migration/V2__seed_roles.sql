-- V2__seed_roles.sql
INSERT INTO roles (name, description) VALUES
 ('ADMIN','Administrador'),
 ('MEDICO','Profissional médico'),
 ('ESCALISTA','Gestor de escalas'),
 ('HOSPITAL_ADMIN', 'Administrador do Hospital')
ON CONFLICT (name) DO NOTHING;