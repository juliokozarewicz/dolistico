#!/bin/bash

# ============================================================== ( set default init )
set -e
REDIS_CLI="redis-cli"
echo "[CACHE INIT] Testing connection..."
$REDIS_CLI PING || { echo "Redis not accessible"; exit 1; }

echo "[CACHE INIT] Setting password for default user..."
$REDIS_CLI ACL SETUSER default on ">$CACHE_ADMIN_PASSWORD"
export REDISCLI_AUTH="$CACHE_ADMIN_PASSWORD"
redis-cli PING
# =============================================================== ( set default end )

# ================================================================= ( accounts init )
if [ -n "$ACCOUNTS_CACHE_USER" ] && [ -n "$ACCOUNTS_CACHE_PASSWORD" ]; then
  echo "[CACHE INIT] Creating Accounts user..."

  redis-cli ACL SETUSER "$ACCOUNTS_CACHE_USER" on \
      ">$ACCOUNTS_CACHE_PASSWORD" \
      resetkeys resetchannels \
      ~accounts-* \
      +@read +@write \
      +publish +subscribe +psubscribe \
      -scan -keys -type -ttl -pttl -@dangerous -@admin \
      "&__keyevent@0__:expired"
fi
# ================================================================== ( accounts end )

# ==================================================================== ( tasks init )
if [ -n "$TASKS_CACHE_USER" ] && [ -n "$TASKS_CACHE_PASSWORD" ]; then
  echo "[CACHE INIT] Creating Tasks user..."

  redis-cli ACL SETUSER "$TASKS_CACHE_USER" on \
      ">$TASKS_CACHE_PASSWORD" \
      resetkeys resetchannels \
      ~tasks-* \
      +@read +@write \
      +publish +subscribe +psubscribe \
      -scan -keys -type -ttl -pttl -@dangerous -@admin \
      "&__keyevent@0__:expired"
fi
# ===================================================================== ( tasks end )

echo "[CACHE INIT] Completed successfully"