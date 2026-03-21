#!/bin/bash
set -euo pipefail

# ============================================================= ( config )
: "${MESSAGING_PORT:?Missing MESSAGING_PORT}"
: "${MESSAGING_ADMIN_USER:?Missing MESSAGING_ADMIN_USER}"
: "${MESSAGING_ADMIN_PASSWORD:?Missing MESSAGING_ADMIN_PASSWORD}"
: "${ACCOUNTS_MESSAGING_USER:?Missing ACCOUNTS_MESSAGING_USER}"
: "${ACCOUNTS_MESSAGING_PASSWORD:?Missing ACCOUNTS_MESSAGING_PASSWORD}"
: "${TASKS_MESSAGING_USER:?Missing TASKS_MESSAGING_USER}"
: "${TASKS_MESSAGING_PASSWORD:?Missing TASKS_MESSAGING_PASSWORD}"

KAFKA_BIN="/opt/kafka/bin"
BOOTSTRAP="messaging:${MESSAGING_PORT}"

# Todas as operações usam SASL — o Kafka já sobe com SASL habilitado
ADMIN_CONFIG="/tmp/admin.properties"
cat > "$ADMIN_CONFIG" << ADMINCFG
bootstrap.servers=${BOOTSTRAP}
security.protocol=SASL_PLAINTEXT
sasl.mechanism=SCRAM-SHA-256
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="${MESSAGING_ADMIN_USER}" password="${MESSAGING_ADMIN_PASSWORD}";
client.dns.lookup=use_all_dns_ips
ADMINCFG
# ============================================================== ( config end )

# ============================================================= ( wait for kafka )
echo "[MESSAGING INIT] Waiting for Kafka to be ready..."
until $KAFKA_BIN/kafka-broker-api-versions.sh \
  --bootstrap-server "$BOOTSTRAP" \
  --command-config "$ADMIN_CONFIG" > /dev/null 2>&1; do
  echo "[MESSAGING INIT] Kafka not ready yet, retrying..."
  sleep 3
done
echo "[MESSAGING INIT] Kafka is ready."
# ============================================================== ( wait end )

echo "***************************** [ INIT SCRIPT ] ****************************"

# ------------------------------------------------------------- ( helpers )
create_user() {
  local user=$1
  local password=$2

  [ -z "$user" ] || [ -z "$password" ] && return

  echo "[MESSAGING INIT] Creating user: $user"
  $KAFKA_BIN/kafka-configs.sh \
    --bootstrap-server "$BOOTSTRAP" \
    --command-config "$ADMIN_CONFIG" \
    --alter \
    --add-config "SCRAM-SHA-256=[password=${password}]" \
    --entity-type users \
    --entity-name "$user" || true
}

create_topic() {
  local topic=$1
  local partitions=${2:-1}
  local replication=${3:-1}
  local retention_ms=${4:-604800000}

  [ -z "$topic" ] && return

  echo "[MESSAGING INIT] Creating topic: $topic"
  $KAFKA_BIN/kafka-topics.sh \
    --bootstrap-server "$BOOTSTRAP" \
    --command-config "$ADMIN_CONFIG" \
    --create \
    --if-not-exists \
    --topic "$topic" \
    --partitions "$partitions" \
    --replication-factor "$replication" \
    --config retention.ms="$retention_ms" || true
}

grant_acls() {
  local user=$1
  local prefix=$2

  [ -z "$user" ] || [ -z "$prefix" ] && return

  echo "[MESSAGING INIT] Granting ACLs for user: $user (prefix: $prefix)"

  # Shared topic: send.simple.email.v1
  $KAFKA_BIN/kafka-acls.sh \
    --bootstrap-server "$BOOTSTRAP" \
    --command-config "$ADMIN_CONFIG" \
    --add --allow-principal "User:${user}" \
    --operation WRITE --operation DESCRIBE \
    --topic "send.simple.email.v1" || true

  # Service-owned topics (prefix.*)
  $KAFKA_BIN/kafka-acls.sh \
    --bootstrap-server "$BOOTSTRAP" \
    --command-config "$ADMIN_CONFIG" \
    --add --allow-principal "User:${user}" \
    --operation ALL \
    --topic "${prefix}." \
    --resource-pattern-type prefixed || true

  # Consumer groups (prefix.*)
  $KAFKA_BIN/kafka-acls.sh \
    --bootstrap-server "$BOOTSTRAP" \
    --command-config "$ADMIN_CONFIG" \
    --add --allow-principal "User:${user}" \
    --operation READ --operation DESCRIBE \
    --group "${prefix}." \
    --resource-pattern-type prefixed || true

  # Allow topic creation
  $KAFKA_BIN/kafka-acls.sh \
    --bootstrap-server "$BOOTSTRAP" \
    --command-config "$ADMIN_CONFIG" \
    --add --allow-principal "User:${user}" \
    --operation CREATE --cluster || true
}
# -------------------------------------------------------------- ( helpers end )

# ============================================================= ( users )
create_user "$MESSAGING_ADMIN_USER"    "$MESSAGING_ADMIN_PASSWORD"
create_user "$ACCOUNTS_MESSAGING_USER" "$ACCOUNTS_MESSAGING_PASSWORD"
create_user "$TASKS_MESSAGING_USER"    "$TASKS_MESSAGING_PASSWORD"
# ============================================================== ( users end )

# ============================================================= ( topics )
create_topic "send.simple.email.v1"
# ============================================================== ( topics end )

# ============================================================= ( acls )
grant_acls "$ACCOUNTS_MESSAGING_USER" "accounts"
grant_acls "$TASKS_MESSAGING_USER"    "tasks"
# ============================================================== ( acls end )

echo "[MESSAGING INIT] Setup complete."
echo "***************************** [ END SCRIPT ] ****************************"