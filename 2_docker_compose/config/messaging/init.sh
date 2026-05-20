#!/bin/bash
set -euo pipefail

KAFKA_BIN="/opt/kafka/bin"
BOOTSTRAP="messaging:${MESSAGING_PORT}"

ADMIN_CONFIG="/tmp/admin.properties"
cat > "$ADMIN_CONFIG" << EOF
    bootstrap.servers=${BOOTSTRAP}
    security.protocol=SASL_PLAINTEXT
    sasl.mechanism=SCRAM-SHA-256
    sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="${MESSAGING_ADMIN_USER}" password="${MESSAGING_ADMIN_PASSWORD}";
    client.dns.lookup=use_all_dns_ips
EOF

echo "[MESSAGING INIT] Waiting for Kafka..."
until $KAFKA_BIN/kafka-broker-api-versions.sh --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" > /dev/null 2>&1; do
    sleep 3
done

echo "***************************** [ INIT SCRIPT ] ****************************"

create_user() {

  local user=$1 password=$2 prefix=$3

  echo "[MESSAGING INIT] Creating user: $user"
  $KAFKA_BIN/kafka-configs.sh \
      --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" \
      --alter --add-config "SCRAM-SHA-256=[password=${password}]" \
      --entity-type users --entity-name "$user" || true

  $KAFKA_BIN/kafka-acls.sh \
      --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" \
      --add --allow-principal "User:${user}" \
      --operation WRITE --operation DESCRIBE \
      --topic "send.simple.email.v1" || true

  $KAFKA_BIN/kafka-acls.sh \
      --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" \
      --add --allow-principal "User:${user}" \
      --operation ALL --topic "${prefix}." --resource-pattern-type prefixed || true

  $KAFKA_BIN/kafka-acls.sh \
      --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" \
      --add --allow-principal "User:${user}" \
      --operation READ --operation DESCRIBE \
      --group "${prefix}." --resource-pattern-type prefixed || true

  $KAFKA_BIN/kafka-acls.sh \
      --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" \
      --add --allow-principal "User:${user}" \
      --operation CREATE \
      --topic "${prefix}." \
      --resource-pattern-type prefixed || true

}

# ------------------------------------------------- ( create basic topics init )

$KAFKA_BIN/kafka-configs.sh \
    --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" \
    --alter --add-config "SCRAM-SHA-256=[password=${MESSAGING_ADMIN_PASSWORD}]" \
    --entity-type users --entity-name "$MESSAGING_ADMIN_USER" || true

$KAFKA_BIN/kafka-topics.sh \
    --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" \
    --create --if-not-exists --topic "send.simple.email.v1" \
    --partitions 1 --replication-factor 1 --config retention.ms=604800000 || true

# -------------------------------------------------- ( create basic topics end )

# --------------------------------------------------------- ( call create init )

create_user "$TASKS_MESSAGING_USER"    "$TASKS_MESSAGING_PASSWORD"    "tasks"
create_user "$ACCOUNTS_MESSAGING_USER" "$ACCOUNTS_MESSAGING_PASSWORD" "accounts"
create_user "$EMAIL_SERVICE_MESSAGING_USER" "$EMAIL_SERVICE_MESSAGING_PASSWORD" "email.service"

# ---------------------------------------------------------- ( call create end )

# ---------------------------------------------------------- ( post rules init )
$KAFKA_BIN/kafka-acls.sh \
    --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" \
    --add --allow-principal "User:${EMAIL_SERVICE_MESSAGING_USER}" \
    --operation READ --operation DESCRIBE \
    --topic "send.simple.email.v1"
# ----------------------------------------------------------- ( post rules end )

echo "***************************** [ END SCRIPT ] *****************************"