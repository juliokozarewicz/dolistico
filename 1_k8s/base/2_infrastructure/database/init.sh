#!/bin/bash
set -e

export PGPASSWORD="$DATABASE_ADMIN_PASSWORD"

echo "Waiting for Postgres..."
until pg_isready -h database -U "$DATABASE_ADMIN_USER" -d postgres; do sleep 2; done
echo "Postgres is ready!"

# ------------------------------------------------------------- ( helpers init )

create_user() {
  local user=$1 password=$2
  psql -v ON_ERROR_STOP=1 -h database -U "$DATABASE_ADMIN_USER" -d postgres <<-EOF
    DO \$\$ BEGIN
      IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = '$user') THEN
        CREATE USER "$user";
      END IF;
    END \$\$;
    ALTER USER "$user" WITH PASSWORD '$password';
EOF
}

create_database() {
  local db=$1 owner=$2

  psql -v ON_ERROR_STOP=1 -h database -U "$DATABASE_ADMIN_USER" -d postgres <<-EOF
    SELECT 'CREATE DATABASE "$db"'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '$db')\gexec

    ALTER DATABASE "$db" OWNER TO "$owner";
    REVOKE ALL ON DATABASE "$db" FROM PUBLIC;
    GRANT CONNECT ON DATABASE "$db" TO "$owner";
EOF
}

create_schema() {
  local db=$1 schema=$2 owner=$3
  psql -v ON_ERROR_STOP=1 -h database -U "$DATABASE_ADMIN_USER" -d "$db" <<-EOF
    REVOKE ALL ON SCHEMA public FROM PUBLIC;
    CREATE SCHEMA IF NOT EXISTS "$schema";
    ALTER SCHEMA "$schema" OWNER TO "$owner";
    GRANT ALL PRIVILEGES ON SCHEMA "$schema" TO "$owner";
    GRANT CREATE ON SCHEMA "$schema" TO "$owner";
    GRANT ALL PRIVILEGES ON ALL TABLES    IN SCHEMA "$schema" TO "$owner";
    GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "$schema" TO "$owner";
    GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA "$schema" TO "$owner";
    ALTER DEFAULT PRIVILEGES IN SCHEMA "$schema" GRANT ALL PRIVILEGES ON TABLES    TO "$owner";
    ALTER DEFAULT PRIVILEGES IN SCHEMA "$schema" GRANT ALL PRIVILEGES ON SEQUENCES TO "$owner";
    ALTER DEFAULT PRIVILEGES IN SCHEMA "$schema" GRANT ALL PRIVILEGES ON FUNCTIONS TO "$owner";
    ALTER ROLE "$owner" SET search_path = "$schema";
EOF
}

# -------------------------------------------------------------- ( helpers end )

# ------------------------------------------------------------ ( accounts init )

create_user     "$ACCOUNTS_DATABASE_USER" "$ACCOUNTS_DATABASE_PASSWORD"
create_database "$ACCOUNTS_DATABASE_NAME" "$ACCOUNTS_DATABASE_USER"
create_schema   "$ACCOUNTS_DATABASE_NAME" "$ACCOUNTS_DATABASE_SCHEMA" "$ACCOUNTS_DATABASE_USER"

# ------------------------------------------------------------- ( accounts end )

# --------------------------------------------------------------- ( tasks init )

create_user     "$TASKS_DATABASE_USER" "$TASKS_DATABASE_PASSWORD"
create_database "$TASKS_DATABASE_NAME" "$TASKS_DATABASE_USER"
create_schema   "$TASKS_DATABASE_NAME" "$TASKS_DATABASE_SCHEMA" "$TASKS_DATABASE_USER"

# ---------------------------------------------------------------- ( tasks end )

echo "Done."
