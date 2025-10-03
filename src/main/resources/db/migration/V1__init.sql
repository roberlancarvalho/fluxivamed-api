-- Criar tabela de usuários
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(150),
    enabled BOOLEAN DEFAULT TRUE,
    tenant_id VARCHAR(50),  -- multi-clínica/hospital
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

-- Criar tabela de roles (perfis de acesso)
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE, -- ex.: ADMIN, MEDICO, ESCALISTA
    description VARCHAR(150)
);

-- Relação N:N entre users e roles
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- Índices para melhorar performance em buscas
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_roles_name ON roles (name);

-- Inserir roles iniciais
INSERT INTO roles (name, description) VALUES 
    ('ADMIN', 'Administrador do sistema'),
    ('MEDICO', 'Profissional médico'),
    ('ESCALISTA', 'Responsável pela gestão de escalas')
ON CONFLICT (name) DO NOTHING;
