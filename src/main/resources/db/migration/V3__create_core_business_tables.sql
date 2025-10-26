-- V3__create_core_business_tables.sql
CREATE TABLE IF NOT EXISTS hospitais (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    endereco TEXT
);

CREATE TABLE IF NOT EXISTS medicos (
    user_id BIGINT PRIMARY KEY,
    crm VARCHAR(20) NOT NULL UNIQUE,
    -- especialidade_id será adicionada em uma migration futura,
    -- mas por enquanto, precisa estar aqui para o V4.
    especialidade_id BIGINT, -- Temporariamente permitindo NULL para V4 rodar antes de V8/V9
    CONSTRAINT fk_medicos_users FOREIGN KEY (user_id) REFERENCES users(id)
    -- CONSTRAINT fk_medicos_especialidade FOREIGN KEY (especialidade_id) REFERENCES especialidades(id) -- Descomentar depois que especialidades for criada
);

CREATE TABLE IF NOT EXISTS plantoes (
    id BIGSERIAL PRIMARY KEY,
    hospital_id BIGINT NOT NULL,
    medico_id BIGINT,
    inicio TIMESTAMP NOT NULL,
    fim TIMESTAMP NOT NULL,
    valor DOUBLE PRECISION NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_plantoes_hospitais FOREIGN KEY (hospital_id) REFERENCES hospitais(id),
    CONSTRAINT fk_plantoes_medicos FOREIGN KEY (medico_id) REFERENCES medicos(user_id)
);