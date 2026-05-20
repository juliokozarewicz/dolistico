#!/bin/bash
set -euo pipefail

# Kafka binaries directory
KAFKA_BIN="/opt/kafka/bin"

# Kafka bootstrap server address
BOOTSTRAP="messaging:${MESSAGING_PORT}"

# Admin client properties file
ADMIN_CONFIG="/tmp/admin.properties"

# Create Kafka admin config file
cat > "$ADMIN_CONFIG" << EOF
    bootstrap.servers=${BOOTSTRAP}
    security.protocol=SASL_PLAINTEXT
    sasl.mechanism=SCRAM-SHA-256
    sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username="${MESSAGING_ADMIN_USER}" password="${MESSAGING_ADMIN_PASSWORD}";
    client.dns.lookup=use_all_dns_ips
EOF

# Wait for Kafka broker to become available
echo "[MESSAGING INIT] Waiting for Kafka..."
until $KAFKA_BIN/kafka-broker-api-versions.sh --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" > /dev/null 2>&1; do
    sleep 3
done

echo "***************************** [ INIT SCRIPT ] ****************************"

# Function to create Kafka user and configure ACLs
create_user() {

  local user=$1 password=$2 prefix=$3

  echo "[MESSAGING INIT] Creating user: $user"

  # Set SCRAM credentials for user
  $KAFKA_BIN/kafka-configs.sh \
      --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" \
      --alter --add-config "SCRAM-SHA-256=[password=${password}]" \
      --entity-type users --entity-name "$user" || true

  # Grant write and describe access to specific topic
  $KAFKA_BIN/kafka-acls.sh \
      --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" \
      --add --allow-principal "User:${user}" \
      --operation WRITE --operation DESCRIBE \
      --topic "send.simple.email.v1" || true

  # Grant full access to prefixed topics
  $KAFKA_BIN/kafka-acls.sh \
      --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" \
      --add --allow-principal "User:${user}" \
      --operation ALL --topic "${prefix}." --resource-pattern-type prefixed || true

  # Grant read and describe access to prefixed consumer groups
  $KAFKA_BIN/kafka-acls.sh \
      --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" \
      --add --allow-principal "User:${user}" \
      --operation READ --operation DESCRIBE \
      --group "${prefix}." --resource-pattern-type prefixed || true

  # Allow creating prefixed topics
  $KAFKA_BIN/kafka-acls.sh \
      --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" \
      --add --allow-principal "User:${user}" \
      --operation CREATE \
      --topic "${prefix}." \
      --resource-pattern-type prefixed || true

}

# ------------------------------------------------- ( create basic topics init )

# Configure SCRAM for admin user
$KAFKA_BIN/kafka-configs.sh \
    --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" \
    --alter --add-config "SCRAM-SHA-256=[password=${MESSAGING_ADMIN_PASSWORD}]" \
    --entity-type users --entity-name "$MESSAGING_ADMIN_USER" || true

# Create email topic if not exists
$KAFKA_BIN/kafka-topics.sh \
    --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" \
    --create --if-not-exists --topic "send.simple.email.v1" \
    --partitions 1 --replication-factor 1 --config retention.ms=604800000 || true

# ACLs for email service user
$KAFKA_BIN/kafka-acls.sh \
    --bootstrap-server "$BOOTSTRAP" --command-config "$ADMIN_CONFIG" \
    --add --allow-principal "User:${EMAIL_SERVICE_MESSAGING_USER}" \
    --operation READ --operation DESCRIBE \
    --topic "send.simple.email.v1"
# -------------------------------------------------- ( create basic topics end )

# --------------------------------------------------------- ( call create init )

# Create application users
create_user "$TASKS_MESSAGING_USER"    "$TASKS_MESSAGING_PASSWORD"    "tasks"
create_user "$ACCOUNTS_MESSAGING_USER" "$ACCOUNTS_MESSAGING_PASSWORD" "accounts"
create_user "$EMAIL_SERVICE_MESSAGING_USER" "$EMAIL_SERVICE_MESSAGING_PASSWORD" "email.service"

# ---------------------------------------------------------- ( call create end )

echo "***************************** [ END SCRIPT ] *****************************"