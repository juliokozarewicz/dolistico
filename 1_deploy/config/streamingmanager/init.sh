#!/bin/bash
set -euo pipefail

# =========================================================== ( Start Redpanda init )
: "${STREAMINGMANAGER_PORT:?Missing STREAMINGMANAGER_PORT}"

DATA_DIR=/var/lib/redpanda
SASL_FLAG="${DATA_DIR}/.sasl_enabled"

if rpk cluster config get enable_sasl 2>/dev/null | grep -q true; then
  export RPK_SASL_MECHANISM=SCRAM-SHA-256
  export RPK_SASL_USERNAME="${STREAMINGMANAGER_ADMIN_USER}"
  export RPK_SASL_PASSWORD="${STREAMINGMANAGER_ADMIN_PASSWORD}"
fi

rpk redpanda start \
  --overprovisioned \
  --smp 1 \
  --memory 1G \
  --reserve-memory 0M \
  --node-id 0 \
  --kafka-addr PLAINTEXT://0.0.0.0:${STREAMINGMANAGER_PORT} \
  --advertise-kafka-addr PLAINTEXT://streamingmanager:${STREAMINGMANAGER_PORT} &

REDPANDA_PID=$!

sleep 15

export RPK_BROKERS=streamingmanager:${STREAMINGMANAGER_PORT}
# ============================================================ ( Start Redpanda end )

if ! rpk cluster config get enable_sasl 2>/dev/null | grep -q true; then

  # ============================================================= ( Admin user init )
  : "${STREAMINGMANAGER_ADMIN_USER:?Missing STREAMINGMANAGER_ADMIN_USER}"
  : "${STREAMINGMANAGER_ADMIN_PASSWORD:?Missing STREAMINGMANAGER_ADMIN_PASSWORD}"

  rpk acl user create "${STREAMINGMANAGER_ADMIN_USER}" \
    --password "${STREAMINGMANAGER_ADMIN_PASSWORD}" \
    --mechanism SCRAM-SHA-256 || true
  # ============================================================== ( Admin user end )

  # ================================================================= ( Topics init )
  # Creating required topics
  rpk topic create send.simple.email.v1 --if-not-exists
  # ================================================================== ( Topics end )

  # ========================================================== ( Accounts user init )

  # Create user
  # ---------------------------------------------------------------------------------
  : "${ACCOUNTS_STREAMINGMANAGER_USER:?Missing ACCOUNTS_STREAMINGMANAGER_USER}"
  : "${ACCOUNTS_STREAMINGMANAGER_PASSWORD:?Missing ACCOUNTS_STREAMINGMANAGER_PASSWORD}"

  rpk acl user create "${ACCOUNTS_STREAMINGMANAGER_USER}" \
    --password "${ACCOUNTS_STREAMINGMANAGER_PASSWORD}" \
    --mechanism SCRAM-SHA-256 || true
  # ---------------------------------------------------------------------------------

  # Common topics
  # ---------------------------------------------------------------------------------
  rpk acl create \
    --allow-principal "User:${ACCOUNTS_STREAMINGMANAGER_USER}" \
    --operation WRITE \
    --topic send.simple.email.v1 \
    --resource-pattern-type literal || true

  rpk acl create \
    --allow-principal "User:${ACCOUNTS_STREAMINGMANAGER_USER}" \
    --operation DESCRIBE \
    --topic send.simple.email.v1 \
    --resource-pattern-type literal || true
  # ---------------------------------------------------------------------------------

  # Service topics
  # ---------------------------------------------------------------------------------
  # Allow full control over own topics (accounts.*)
  rpk acl create \
    --allow-principal "User:${ACCOUNTS_STREAMINGMANAGER_USER}" \
    --operation ALL \
    --topic "accounts." \
    --resource-pattern-type prefixed || true

  # Allow consumer group usage for own groups (accounts.*)
  rpk acl create \
    --allow-principal "User:${ACCOUNTS_STREAMINGMANAGER_USER}" \
    --operation READ \
    --group "accounts." \
    --resource-pattern-type prefixed || true

  rpk acl create \
    --allow-principal "User:${ACCOUNTS_STREAMINGMANAGER_USER}" \
    --operation DESCRIBE \
    --group "accounts." \
    --resource-pattern-type prefixed || true

  # Allow topic creation (only names are still restricted by topic ACLs)
  rpk acl create \
    --allow-principal "User:${ACCOUNTS_STREAMINGMANAGER_USER}" \
    --operation CREATE \
    --cluster || true
  # ---------------------------------------------------------------------------------

  # =========================================================== ( Accounts user end )

  # ============================================================= ( Apply  SASL init)
  rpk cluster config set enable_sasl true
  rpk cluster config set superusers "[\"${STREAMINGMANAGER_ADMIN_USER}\"]"
  touch "$SASL_FLAG"
  kill "${REDPANDA_PID}"
  wait "${REDPANDA_PID}" || true
  sleep 5
  export RPK_SASL_MECHANISM=SCRAM-SHA-256
  export RPK_SASL_USERNAME="${STREAMINGMANAGER_ADMIN_USER}"
  export RPK_SASL_PASSWORD="${STREAMINGMANAGER_ADMIN_PASSWORD}"

  exec rpk redpanda start \
    --overprovisioned \
    --smp 1 \
    --memory 1G \
    --reserve-memory 0M \
    --node-id 0 \
    --kafka-addr PLAINTEXT://0.0.0.0:${STREAMINGMANAGER_PORT} \
    --advertise-kafka-addr PLAINTEXT://streamingmanager:${STREAMINGMANAGER_PORT}
  # ============================================================== ( Apply  SASL end)

fi

wait "$REDPANDA_PID"