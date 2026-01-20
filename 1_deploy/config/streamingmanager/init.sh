#!/bin/bash
set -e

rpk redpanda start \
  --overprovisioned \
  --smp 1 \
  --memory 1G \
  --reserve-memory 0M \
  --node-id 0 \
  --kafka-addr PLAINTEXT://0.0.0.0:${STREAMINGMANAGER_PORT} \
  --advertise-kafka-addr PLAINTEXT://streamingmanager:${STREAMINGMANAGER_PORT} \
  --set redpanda.enable_sasl=true \
  --set redpanda.superusers='["${STREAMINGMANAGER_ADMIN_USER}"]' &

REDPANDA_PID=$!

sleep 10

######################################################################## (admin init)
rpk acl user create ${STREAMINGMANAGER_ADMIN_USER} \
  --password ${STREAMINGMANAGER_ADMIN_PASSWORD} \
  --mechanism SCRAM-SHA-256 || true

# Cluster admin
rpk acl create \
  --allow-principal User:${STREAMINGMANAGER_ADMIN_USER} \
  --operation ALL \
  --cluster || true

# All topics
rpk acl create \
  --allow-principal User:${STREAMINGMANAGER_ADMIN_USER} \
  --operation ALL \
  --topic '*' || true

# All consumer groups
rpk acl create \
  --allow-principal User:${STREAMINGMANAGER_ADMIN_USER} \
  --operation ALL \
  --group '*' || true

# All transactional IDs
rpk acl create \
  --allow-principal User:${STREAMINGMANAGER_ADMIN_USER} \
  --operation ALL \
  --transactional-id '*' || true
######################################################################### (admin end)

echo "#########################################"
echo "Redpanda and users successfully configured."
echo "#########################################"

wait $REDPANDA_PID