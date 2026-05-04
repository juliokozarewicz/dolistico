CREATE SCHEMA IF NOT EXISTS accounts;

-- ACCOUNT PROFILE
CREATE TABLE IF NOT EXISTS accounts.accounts_profile (
    id UUID PRIMARY KEY,
    id_user UUID NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    full_name VARCHAR(256),
    phone VARCHAR(25),
    identity_document VARCHAR(256),
    gender VARCHAR(256),
    birthdate VARCHAR(50),
    biography VARCHAR(256),
    profile_image VARCHAR(555),
    language VARCHAR(50),
    theme VARCHAR(100)
);