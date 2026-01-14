-- ==================== СПРАВОЧНИКИ ====================
CREATE TABLE user_role (
    code VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE insurance_type (
    code VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(100),
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE contract_status (
    code VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE payment_status (
    code VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE claim_status (
    code VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- ==================== ОСНОВНЫЕ СУЩНОСТИ ====================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_code VARCHAR(50) REFERENCES user_role(code),
    full_name VARCHAR(200) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE client (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(200) NOT NULL,
    passport_series VARCHAR(10),
    passport_number VARCHAR(20),
    phone VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE contract (
    id BIGSERIAL PRIMARY KEY,
    contract_number VARCHAR(100) UNIQUE NOT NULL,
    client_id BIGINT REFERENCES client(id) ON DELETE CASCADE,
    insurance_type_code VARCHAR(50) REFERENCES insurance_type(code),
    agent_id BIGINT REFERENCES users(id),
    status_code VARCHAR(50) REFERENCES contract_status(code),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    premium_amount DECIMAL(15, 2) NOT NULL CHECK (premium_amount >= 0),
    insured_amount DECIMAL(15, 2) NOT NULL CHECK (insured_amount >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT contract_dates_check CHECK (end_date > start_date)
);

CREATE TABLE payment (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT REFERENCES contract(id) ON DELETE CASCADE,
    amount DECIMAL(15, 2) NOT NULL CHECK (amount > 0),
    payment_date DATE DEFAULT CURRENT_DATE,
    status_code VARCHAR(50) REFERENCES payment_status(code),
    payment_method VARCHAR(50),
    transaction_number VARCHAR(100)
);

CREATE TABLE insurance_claim (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT REFERENCES contract(id) ON DELETE CASCADE,
    claim_number VARCHAR(100) UNIQUE NOT NULL,
    incident_date DATE NOT NULL,
    description TEXT,
    claimed_amount DECIMAL(15, 2) CHECK (claimed_amount >= 0),
    approved_amount DECIMAL(15, 2) CHECK (approved_amount >= 0),
    status_code VARCHAR(50) REFERENCES claim_status(code),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== СПЕЦИФИЧНЫЕ ДАННЫЕ ====================
CREATE TABLE osago_data (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT UNIQUE REFERENCES contract(id) ON DELETE CASCADE,
    license_plate VARCHAR(20),
    vehicle_model VARCHAR(100) NOT NULL,
    vin VARCHAR(50),
    driving_experience INTEGER CHECK (driving_experience >= 0)
);

CREATE TABLE casco_data (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT UNIQUE REFERENCES contract(id) ON DELETE CASCADE,
    vehicle_model VARCHAR(100) NOT NULL,
    manufacture_year INTEGER CHECK (
        manufacture_year > 1900
        AND manufacture_year <= EXTRACT(YEAR FROM CURRENT_DATE)
    ),
    vehicle_cost DECIMAL(15, 2) NOT NULL CHECK (vehicle_cost > 0),
    has_franchise BOOLEAN DEFAULT FALSE,
    franchise_amount DECIMAL(15, 2) DEFAULT 0 CHECK (franchise_amount >= 0)
);

CREATE TABLE life_insurance_data (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT UNIQUE REFERENCES contract(id) ON DELETE CASCADE,
    birth_date DATE NOT NULL CHECK (birth_date < CURRENT_DATE),
    gender VARCHAR(10) CHECK (
        gender IN ('M', 'F', 'Male', 'Female', 'Мужской', 'Женский')
    ),
    profession VARCHAR(100),
    health_status VARCHAR(200)
);

CREATE TABLE property_insurance_data (
    id BIGSERIAL PRIMARY KEY,
    contract_id BIGINT UNIQUE REFERENCES contract(id) ON DELETE CASCADE,
    property_type VARCHAR(100) NOT NULL,
    address VARCHAR(200) NOT NULL,
    area DECIMAL(10, 2) CHECK (area > 0),
    construction_year INTEGER CHECK (
        construction_year > 0
        AND construction_year <= EXTRACT(YEAR FROM CURRENT_DATE)
    ),
    cost DECIMAL(15, 2) NOT NULL CHECK (cost > 0)
);

-- ==================== ИНДЕКСЫ ====================
CREATE INDEX idx_contract_client ON contract (client_id);
CREATE INDEX idx_contract_agent ON contract (agent_id);
CREATE INDEX idx_contract_status ON contract (status_code);
CREATE INDEX idx_contract_type ON contract (insurance_type_code);
CREATE INDEX idx_payment_contract ON payment (contract_id);
CREATE INDEX idx_payment_status ON payment (status_code);
CREATE INDEX idx_claim_contract ON insurance_claim (contract_id);
CREATE INDEX idx_claim_status ON insurance_claim (status_code);
CREATE INDEX idx_users_role ON users (role_code);

-- ==================== ТЕСТОВЫЕ ДАННЫЕ ====================
INSERT INTO user_role (code, name) VALUES
    ('ADMIN', 'Администратор'),
    ('AGENT', 'Агент'),
    ('MANAGER', 'Менеджер');

INSERT INTO insurance_type (code, name, category, active) VALUES
    ('OSAGO', 'ОСАГО', 'Авто', true),
    ('CASCO', 'КАСКО', 'Авто', true),
    ('LIFE', 'Страхование жизни', 'Жизнь', true),
    ('PROPERTY', 'Страхование недвижимости', 'Недвижимость', true);

INSERT INTO contract_status (code, name) VALUES
    ('DRAFT', 'Черновик'),
    ('ACTIVE', 'Активен'),
    ('EXPIRED', 'Истек'),
    ('TERMINATED', 'Расторгнут'),
    ('SUSPENDED', 'Приостановлен');

INSERT INTO payment_status (code, name) VALUES
    ('PENDING', 'Ожидает оплаты'),
    ('PAID', 'Оплачен'),
    ('FAILED', 'Неуспешный'),
    ('REFUNDED', 'Возвращен'),
    ('PARTIAL', 'Частичная оплата');

INSERT INTO claim_status (code, name) VALUES
    ('NEW', 'Новый'),
    ('IN_REVIEW', 'На рассмотрении'),
    ('APPROVED', 'Одобрен'),
    ('REJECTED', 'Отклонен'),
    ('PAID', 'Выплачен');

INSERT INTO users (login, password, role_code, full_name, active) VALUES
    ('admin', '$2a$10$9vxtFDTf3dw.RJytGocBi.iT.YLLpsMhCLSw8wu4qzXzOpdhtb7l2', 'ADMIN', 'Иванов Иван Иванович', true),
    ('agent1', '$2a$10$xaVWsI8wbHT2uSt3ZxP56u1TZMdsbt7a8tZgd49c2mytoMqkaz.nm', 'AGENT', 'Петров Петр Петрович', true),
    ('manager1', '$2a$10$BY1RbuW01vO2lxg0A7ZTY.6d/BSnGPImOWZeIrVqf4mXU5q1BA.Bm', 'MANAGER', 'Сидорова Мария Сергеевна', true);

INSERT INTO client (full_name, passport_series, passport_number, phone, email) VALUES
    ('Смирнов Алексей Владимирович', '4501', '123456', '+79161234567', 'smirnov@mail.ru'),
    ('Кузнецова Ольга Дмитриевна', '4502', '654321', '+79167654321', 'kuznetsova@gmail.com');
