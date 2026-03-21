#!/bin/bash
set -e

REDIS_HOST="cache"
REDIS_PORT="${CACHE_PORT:-6379}"
REDIS_CLI="redis-cli -h $REDIS_HOST -p $REDIS_PORT"

echo "***************************** [ INIT SCRIPT ] ****************************"

echo "[CACHE INIT] Testing connection..."
if ! $REDIS_CLI -a "$CACHE_ADMIN_PASSWORD" PING 2>/dev/null | grep -q PONG; then
  echo "[CACHE INIT] No auth yet, setting password for default user..."
  $REDIS_CLI PING
  $REDIS_CLI ACL SETUSER default on ">$CACHE_ADMIN_PASSWORD"
else
  echo "[CACHE INIT] Auth already configured, skipping..."
fi

export REDISCLI_AUTH="$CACHE_ADMIN_PASSWORD"
$REDIS_CLI PING

create_user() {
  local user=$1
  local password=$2
  local prefix=$3

  if [ -z "$user" ] || [ -z "$password" ]; then
    return
  fi

  echo "[CACHE INIT] Creating/updating user: $user"
  $REDIS_CLI ACL SETUSER "$user" on \
    ">$password" \
    resetkeys resetchannels \
    ~$prefix-* \
    +@read +@write \
    +publish +subscribe +psubscribe \
    -scan -keys -type -ttl -pttl -@dangerous -@admin \
    "&__keyevent@0__:expired" || true
}

# --------------------------------------------------------- ( call create init )

echo "[CACHE INIT] Creating Accounts user..."
create_user "$ACCOUNTS_CACHE_USER" "$ACCOUNTS_CACHE_PASSWORD" "accounts"

echo "[CACHE INIT] Creating Tasks user..."
create_user "$TASKS_CACHE_USER" "$TASKS_CACHE_PASSWORD" "tasks"

# ---------------------------------------------------------- ( call create end )

echo "***************************** [ END SCRIPT ] *****************************"