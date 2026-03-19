#!/bin/bash
set -e

REDIS_HOST="cache"
REDIS_PORT=6379
REDIS_CLI="redis-cli -h $REDIS_HOST -p $REDIS_PORT"

echo "***************************** [ INIT SCRIPT ] ****************************"

echo "[CACHE INIT] Testing connection..."
$REDIS_CLI PING

echo "[CACHE INIT] Setting password for default user..."
$REDIS_CLI ACL SETUSER default on ">$CACHE_ADMIN_PASSWORD"

export REDISCLI_AUTH="$CACHE_ADMIN_PASSWORD"

$REDIS_CLI PING

# ------------------------------------------------------------- ( helpers init )
create_user() {
  local user=$1
  local password=$2
  local prefix=$3

  if [ -z "$user" ] || [ -z "$password" ]; then
    return
  fi

  $REDIS_CLI ACL SETUSER "$user" on \
    ">$password" \
    resetkeys resetchannels \
    ~$prefix-* \
    +@read +@write \
    +publish +subscribe +psubscribe \
    -scan -keys -type -ttl -pttl -@dangerous -@admin \
    "&__keyevent@0__:expired" || true
}
# -------------------------------------------------------------- ( helpers end )

# ------------------------------------------------------------ ( accounts init )
echo "[CACHE INIT] Creating Accounts user..."
create_user "$ACCOUNTS_CACHE_USER" "$ACCOUNTS_CACHE_PASSWORD" "accounts"
# ------------------------------------------------------------- ( accounts end )

# --------------------------------------------------------------- ( tasks init )
echo "[CACHE INIT] Creating Tasks user..."
create_user "$TASKS_CACHE_USER" "$TASKS_CACHE_PASSWORD" "tasks"
# ---------------------------------------------------------------- ( tasks end )

echo "***************************** [ END SCRIPT ] *****************************"