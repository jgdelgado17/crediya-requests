-- Tabla 'status'
CREATE TABLE IF NOT EXISTS status (
    id SERIAL PRIMARY KEY,
    names VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255)
);

-- Tabla 'type_loan'
CREATE TABLE IF NOT EXISTS type_loan (
    id SERIAL PRIMARY KEY,
    names VARCHAR(50) UNIQUE NOT NULL,
    min_amount NUMERIC(19, 2) NOT NULL,
    max_amount NUMERIC(19, 2) NOT NULL,
    interest_rate NUMERIC(5, 2) NOT NULL,
    automatic_validation BOOLEAN
);

-- Tabla 'requests'
CREATE TABLE IF NOT EXISTS requests (
    id SERIAL PRIMARY KEY,
    amount NUMERIC(19, 2) NOT NULL,
    term INTEGER NOT NULL,
    email VARCHAR(100) NOT NULL,
    status INTEGER NOT NULL,
    type_loan INTEGER NOT NULL,
    CONSTRAINT fk_status
        FOREIGN KEY (status)
        REFERENCES status(id),
    CONSTRAINT fk_type_loan
        FOREIGN KEY (type_loan)
        REFERENCES type_loan(id)
);