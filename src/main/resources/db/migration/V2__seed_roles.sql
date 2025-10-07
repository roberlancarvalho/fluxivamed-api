INSERT INTO roles (name, description) VALUES
 ('ADMIN','Administrador'),
 ('MEDICO','Profissional médico'),
 ('ESCALISTA','Gestor de escalas')
ON CONFLICT (name) DO NOTHING;