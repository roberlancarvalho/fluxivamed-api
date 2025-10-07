-- V3__create_core_business_tables.sql

-- Tabela para armazenar os hospitais
CREATE TABLE hospitais (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    endereco TEXT
);

-- Tabela para o perfil profissional dos médicos, ligada ao usuário de autenticação
CREATE TABLE medicos (
    user_id BIGINT PRIMARY KEY,
    crm VARCHAR(20) NOT NULL UNIQUE,
    especialidade VARCHAR(100) NOT NULL,
    CONSTRAINT fk_medicos_users FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabela central para os plantões
CREATE TABLE plantoes (
    id BIGSERIAL PRIMARY KEY,
    hospital_id BIGINT NOT NULL,
    medico_id BIGINT, -- Pode ser nulo se o plantão estiver disponível
    inicio TIMESTAMP NOT NULL,
    fim TIMESTAMP NOT NULL,
    valor DOUBLE PRECISION NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_plantoes_hospitais FOREIGN KEY (hospital_id) REFERENCES hospitais(id),
    CONSTRAINT fk_plantoes_medicos FOREIGN KEY (medico_id) REFERENCES medicos(user_id)
);