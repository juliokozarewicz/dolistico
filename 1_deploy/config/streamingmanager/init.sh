#!/bin/bash
set -euo pipefail

######################################################### ( Required variables init )
: "${STREAMINGMANAGER_PORT:?Missing STREAMINGMANAGER_PORT}"
: "${STREAMINGMANAGER_ADMIN_PASSWORD:?Missing STREAMINGMANAGER_ADMIN_PASSWORD}"
: "${ACCOUNTS_STREAMINGMANAGER_PASSWORD:?Missing ACCOUNTS_STREAMINGMANAGER_PASSWORD}"
########################################################## ( Required variables end )

############################################################# ( Start Redpanda init )
DATA_DIR=/var/lib/redpanda

unset RPK_SASL_MECHANISM
unset RPK_SASL_USERNAME
unset RPK_SASL_PASSWORD

echo ">> Starting Redpanda WITHOUT SASL"

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
############################################################## ( Start Redpanda end )

################################################################# ( Admin user init )

# Create user
# -----------------------------------------------------------------------------------
rpk acl user create admin \
  --password "${STREAMINGMANAGER_ADMIN_PASSWORD}" \
  --mechanism SCRAM-SHA-256
# -----------------------------------------------------------------------------------

# Creating required topics
rpk topic create send.simple.email.v1

################################################################## ( Admin user end )

############################################################## ( Accounts user init )

# Create user
# -----------------------------------------------------------------------------------
rpk acl user create accountsuser \
  --password "${ACCOUNTS_STREAMINGMANAGER_PASSWORD}" \
  --mechanism SCRAM-SHA-256
# -----------------------------------------------------------------------------------

rpk acl create \
  --allow-principal User:accountsuser \
  --operation ALL \
  --topic "*" \
  --resource-pattern-type literal

rpk acl create \
  --allow-principal User:accountsuser \
  --operation ALL \
  --group "*" \
  --resource-pattern-type literal

############################################################### ( Accounts user end )

################################################################# ( Apply  SASL init)
rpk cluster config set enable_sasl true
rpk cluster config set superusers '["admin"]'
kill "${REDPANDA_PID}"
wait "${REDPANDA_PID}" || true
sleep 5
export RPK_SASL_MECHANISM=SCRAM-SHA-256
export RPK_SASL_USERNAME=admin
export RPK_SASL_PASSWORD="${STREAMINGMANAGER_ADMIN_PASSWORD}"

exec rpk redpanda start \
  --overprovisioned \
  --smp 1 \
  --memory 1G \
  --reserve-memory 0M \
  --node-id 0 \
  --kafka-addr PLAINTEXT://0.0.0.0:${STREAMINGMANAGER_PORT} \
  --advertise-kafka-addr PLAINTEXT://streamingmanager:${STREAMINGMANAGER_PORT}
################################################################## ( Apply  SASL end)