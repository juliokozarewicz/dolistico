CREATE SCHEMA IF NOT EXISTS accounts;

-- ACCOUNT PROFILE
CREATE TABLE IF NOT EXISTS accounts.accounts_profile (
    id_user UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    profile_image VARCHAR(512),
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
    old_value TEXT NOT NULL,
    new_value TEXT NOT NULL
);