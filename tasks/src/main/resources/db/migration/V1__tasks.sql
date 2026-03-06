CREATE SCHEMA IF NOT EXISTS tasks;

-- TASKS
CREATE TABLE IF NOT EXISTS tasks.tasks (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    task_name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    category VARCHAR(255),
    color VARCHAR(20),
    priority INTEGER NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    location VARCHAR(255),
    is_all_day BOOLEAN NOT NULL DEFAULT FALSE,
    reminder_time TIMESTAMP,
    notify_active BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(255) NOT NULL,
    due_date TIMESTAMP
);

-- CATEGORY
CREATE TABLE IF NOT EXISTS tasks.category (
    id VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    category_name VARCHAR(100) NOT NULL
);