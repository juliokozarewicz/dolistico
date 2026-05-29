#!/bin/bash
set -e

echo "***************************** [ INIT SCRIPT ] ****************************"

: "${KC_BOOTSTRAP_ADMIN_USERNAME:?KC_BOOTSTRAP_ADMIN_USERNAME is required}"
: "${KC_BOOTSTRAP_ADMIN_PASSWORD:?KC_BOOTSTRAP_ADMIN_PASSWORD is required}"
: "${ACCOUNTS_KEYCLOAK_REALM:?ACCOUNTS_KEYCLOAK_REALM is required}"
: "${ACCOUNTS_KEYCLOAK_CLIENT_ID:?ACCOUNTS_KEYCLOAK_CLIENT_ID is required}"
: "${ACCOUNTS_KEYCLOAK_CLIENT_SECRET:?ACCOUNTS_KEYCLOAK_CLIENT_SECRET is required}"

KEYCLOAK_URL="http://keycloak:8080"

# ------------------------------------------------------------------------------
# Wait until Keycloak is ready to accept requests
# ------------------------------------------------------------------------------
wait_for_keycloak() {
  echo "Waiting for Keycloak..."
  local retries=6

  until curl -sf --max-time 10 \
    "$KEYCLOAK_URL/realms/master" > /dev/null; do

    retries=$((retries - 1))

    if [ "$retries" -le 0 ]; then
      echo "Keycloak did not become ready in time. Aborting."
      exit 1
    fi

    echo "Still waiting... ($retries retries left)"
    sleep 60
  done

  echo "Keycloak is ready!"
}

# ------------------------------------------------------------------------------
# Retrieve admin access token
# ------------------------------------------------------------------------------
get_admin_token() {
  curl -sf --max-time 60 -X POST \
    "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "grant_type=password" \
    -d "client_id=admin-cli" \
    -d "username=$KC_BOOTSTRAP_ADMIN_USERNAME" \
    -d "password=$KC_BOOTSTRAP_ADMIN_PASSWORD" \
    | grep -o '"access_token":"[^"]*' | cut -d'"' -f4
}

# ------------------------------------------------------------------------------
# Check if admin API is accessible
# If not, assume bootstrap already ran and exit cleanly
# ------------------------------------------------------------------------------
admin_api_available() {
  local token=$1

  curl -sf --max-time 10 \
    -H "Authorization: Bearer $token" \
    "$KEYCLOAK_URL/admin/realms/master" > /dev/null
}

# ------------------------------------------------------------------------------
# Check if realm exists
# ------------------------------------------------------------------------------
realm_exists() {
  local token=$1

  curl -sf --max-time 10 \
    -H "Authorization: Bearer $token" \
    "$KEYCLOAK_URL/admin/realms/$ACCOUNTS_KEYCLOAK_REALM" > /dev/null
}

# ------------------------------------------------------------------------------
# Create realm (idempotent)
# ------------------------------------------------------------------------------
create_realm() {
  local token=$1
  local realm=$2

  echo "Creating realm: $realm"

  curl -sf --max-time 60 -X POST \
    "$KEYCLOAK_URL/admin/realms" \
    -H "Authorization: Bearer $token" \
    -H "Content-Type: application/json" \
    -d "{\"realm\": \"$realm\", \"enabled\": true}" \
    && echo "Realm created." || echo "Realm already exists, skipping."
}

# ------------------------------------------------------------------------------
# Configure realm security policies (tokens, sessions, brute force protection)
# ------------------------------------------------------------------------------
configure_realm() {
  local token=$1
  local realm=$2

  echo "Configuring realm: $realm"

  response=$(curl -s --max-time 60 -X PUT \
    "$KEYCLOAK_URL/admin/realms/$realm" \
    -H "Authorization: Bearer $token" \
    -H "Content-Type: application/json" \
    -d "{
      \"realm\": \"$realm\",
      \"enabled\": true,
      \"verifyEmail\": true,
      \"accessTokenLifespan\": 300,
      \"ssoSessionIdleTimeout\": 1296000,
      \"ssoSessionMaxLifespan\": 2592000,
      \"offlineSessionIdleTimeout\": 2592000,
      \"offlineSessionMaxLifespan\": 2592000,
      \"revokeRefreshToken\": true,
      \"refreshTokenMaxReuse\": 0,
      \"loginWithEmailAllowed\": true,
      \"duplicateEmailsAllowed\": false,
      \"registrationEmailAsUsername\": true,
      \"resetPasswordAllowed\": true,
      \"bruteForceProtected\": true,
      \"failureFactor\": 10,
      \"waitIncrementSeconds\": 60,
      \"quickLoginCheckMilliSeconds\": 1000,
      \"minimumQuickLoginWaitSeconds\": 60,
      \"maxFailureWaitSeconds\": 900,
      \"passwordPolicy\": \"length(12) and maxLength(256)\"
    }" \
    -w "\n%{http_code}")

  http_code=$(echo "$response" | tail -n1)

  if [ "$http_code" -ge 400 ]; then
    echo "Realm configuration failed (HTTP $http_code)"
    exit 1
  fi

  echo "Realm configured."
}

# ------------------------------------------------------------------------------
# Create client (idempotent)
# ------------------------------------------------------------------------------
create_client() {
  local token=$1
  local realm=$2
  local client_id=$3
  local client_secret=$4

  echo "Creating client: $client_id"

  curl -sf --max-time 60 -X POST \
    "$KEYCLOAK_URL/admin/realms/$realm/clients" \
    -H "Authorization: Bearer $token" \
    -H "Content-Type: application/json" \
    -d "{
      \"clientId\": \"$client_id\",
      \"enabled\": true,
      \"clientAuthenticatorType\": \"client-secret\",
      \"secret\": \"$client_secret\",
      \"serviceAccountsEnabled\": true,
      \"standardFlowEnabled\": false,
      \"directAccessGrantsEnabled\": true,
      \"implicitFlowEnabled\": false,
      \"authorizationServicesEnabled\": false,
      \"publicClient\": false,
      \"attributes\": {
        \"use.refresh.tokens\": \"true\"
      }
    }" \
    && echo "Client created." || echo "Client already exists, skipping."
}

# ------------------------------------------------------------------------------
# Assign roles to service account (idempotent)
# ------------------------------------------------------------------------------
assign_service_account_roles() {
  local token=$1
  local realm=$2
  local client_id=$3

  echo "Assigning roles to service account..."

  local client_uuid
  client_uuid=$(curl -sf --max-time 60 \
    "$KEYCLOAK_URL/admin/realms/$realm/clients?clientId=$client_id" \
    -H "Authorization: Bearer $token" \
    | grep -o '"id":"[^"]*' | head -1 | cut -d'"' -f4)

  local sa_user_id
  sa_user_id=$(curl -sf --max-time 60 \
    "$KEYCLOAK_URL/admin/realms/$realm/clients/$client_uuid/service-account-user" \
    -H "Authorization: Bearer $token" \
    | grep -o '"id":"[^"]*' | head -1 | cut -d'"' -f4)

  local realm_mgmt_id
  realm_mgmt_id=$(curl -sf --max-time 60 \
    "$KEYCLOAK_URL/admin/realms/$realm/clients?clientId=realm-management" \
    -H "Authorization: Bearer $token" \
    | grep -o '"id":"[^"]*' | head -1 | cut -d'"' -f4)

  for role in manage-users view-users query-users; do
    local role_json

    role_json=$(curl -sf --max-time 60 \
      "$KEYCLOAK_URL/admin/realms/$realm/clients/$realm_mgmt_id/roles/$role" \
      -H "Authorization: Bearer $token")

    curl -sf --max-time 60 -X POST \
      "$KEYCLOAK_URL/admin/realms/$realm/users/$sa_user_id/role-mappings/clients/$realm_mgmt_id" \
      -H "Authorization: Bearer $token" \
      -H "Content-Type: application/json" \
      -d "[$role_json]" \
      && echo "Role '$role' assigned." || echo "Role '$role' already assigned."
  done
}

# ------------------------------------------------------------------------------
# Disable admin user (final bootstrap step)
# ------------------------------------------------------------------------------
disable_admin_user() {
  local token=$1

  echo "Disabling admin user..."

  local user_id
  user_id=$(curl -sf --max-time 60 \
    "$KEYCLOAK_URL/admin/realms/master/users?username=$KC_BOOTSTRAP_ADMIN_USERNAME" \
    -H "Authorization: Bearer $token" \
    | grep -o '"id":"[^"]*' | head -1 | cut -d'"' -f4)

  if [ -z "$user_id" ]; then
    echo "Admin user not found, skipping."
    return
  fi

  curl -sf --max-time 60 -X PUT \
    "$KEYCLOAK_URL/admin/realms/master/users/$user_id" \
    -H "Authorization: Bearer $token" \
    -H "Content-Type: application/json" \
    -d "{\"enabled\": false}" \
    && echo "Admin user disabled." || echo "Failed to disable admin user."
}

# ------------------------------------------------------------------------------
# INIT FLOW
# ------------------------------------------------------------------------------

wait_for_keycloak

if curl -sf --max-time 10 \
  "$KEYCLOAK_URL/realms/$ACCOUNTS_KEYCLOAK_REALM" > /dev/null; then
  echo "Realm already exists → bootstrap already applied. Exiting."
  exit 0
fi

TOKEN=$(get_admin_token)

if [ -z "$TOKEN" ]; then
  echo "Failed to obtain admin token. Aborting."
  exit 1
fi

echo "Running bootstrap..."

create_realm "$TOKEN" "$ACCOUNTS_KEYCLOAK_REALM"
configure_realm "$TOKEN" "$ACCOUNTS_KEYCLOAK_REALM"

create_client "$TOKEN" \
  "$ACCOUNTS_KEYCLOAK_REALM" \
  "$ACCOUNTS_KEYCLOAK_CLIENT_ID" \
  "$ACCOUNTS_KEYCLOAK_CLIENT_SECRET"

assign_service_account_roles \
  "$TOKEN" \
  "$ACCOUNTS_KEYCLOAK_REALM" \
  "$ACCOUNTS_KEYCLOAK_CLIENT_ID"

disable_admin_user "$TOKEN"

echo "***************************** [ END SCRIPT ] *****************************"