CREATE SCHEMA IF NOT EXISTS accounts;

-- ACCOUNT PROFILE
CREATE TABLE IF NOT EXISTS accounts.accounts_profile (
    id_user UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    avatar VARCHAR(512),
    full_name VARCHAR(256),
    phone VARCHAR(25),
    identity_document VARCHAR(256),
    gender VARCHAR(256),
    birthdate VARCHAR(50),
    biography VARCHAR(256),
    language VARCHAR(50),
    theme VARCHAR(100)
);

-- ACCOUNT LOG
CREATE TABLE IF NOT EXISTS accounts.accounts_event_log (
    id UUID PRIMARY KEY,
    id_user UUID NOT NULL,
    ip_address VARCHAR(256) NOT NULL,
    agent VARCHAR(512) NOT NULL,
    update_type VARCHAR(256) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    old_value TEXT,
    new_value TEXT
);

-- ACCOUNT DEVICE
CREATE TABLE IF NOT EXISTS accounts.accounts_devices (
    id UUID PRIMARY KEY,
    id_user UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    ip_address VARCHAR(256) NOT NULL,
    location VARCHAR(256) NOT NULL,
    device VARCHAR(256) NOT NULL,
    method VARCHAR(256) NOT NULL
);

------------------------------------------------------- ( ACCOUNTS CONFIG INIT )
CREATE TABLE IF NOT EXISTS accounts.accounts_config (
    id UUID PRIMARY KEY,
    config_name VARCHAR(256) NOT NULL UNIQUE,
    config_value VARCHAR(512) NOT NULL
);

INSERT INTO accounts.accounts_config (id, config_name, config_value)
VALUES
    ('7d5b4f5e-2c63-4c0c-8c2d-4d7d9cb2d9c8', 'update_password_url', 'http://localhost/static/public/accounts/update-password'),
    ('4e2f84e1-5d87-4d8b-9d35-0b89b91b8f4e', 'update_email_url', 'http://localhost/static/public/accounts/update-email'),
    ('d3a1c9b7-3b7d-4b2e-a8b1-f2d89d7c6e11', 'delete_account_url', 'http://localhost/static/public/accounts/delete-account')
ON CONFLICT (config_name) DO NOTHING;
-------------------------------------------------------- ( ACCOUNTS CONFIG END )