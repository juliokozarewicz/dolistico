#!/bin/bash
set -e

echo "***************************** [ INIT SCRIPT ] ****************************"

KEYCLOAK_URL="http://keycloak:8080"

# ------------------------------------------------------------------------------
# Wait until Keycloak is ready to accept authentication
# ------------------------------------------------------------------------------
wait_for_keycloak() {
  echo "Waiting for Keycloak..."

  until curl -sf -X POST "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "grant_type=password" \
    -d "client_id=admin-cli" \
    -d "username=$KEYCLOAK_ADMIN_USER" \
    -d "password=$KEYCLOAK_ADMIN_PASSWORD" > /dev/null; do
    sleep 3
  done

  echo "Keycloak is ready!"
}

# ------------------------------------------------------------------------------
# Get admin token
# ------------------------------------------------------------------------------
get_admin_token() {
  curl -sf -X POST "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "grant_type=password" \
    -d "client_id=admin-cli" \
    -d "username=$KEYCLOAK_ADMIN_USER" \
    -d "password=$KEYCLOAK_ADMIN_PASSWORD" \
    | grep -o '"access_token":"[^"]*' | cut -d'"' -f4
}

# ------------------------------------------------------------------------------
# Create realm (idempotent)
# ------------------------------------------------------------------------------
create_realm() {
  local token=$1
  local realm=$2

  echo "Creating realm: $realm"

  curl -sf -X POST "$KEYCLOAK_URL/admin/realms" \
    -H "Authorization: Bearer $token" \
    -H "Content-Type: application/json" \
    -d "{\"realm\": \"$realm\", \"enabled\": true}" \
    && echo "Realm created." || echo "Realm already exists, skipping."
}

# ------------------------------------------------------------------------------
# Configure realm security policies (tokens + sessions)
# ------------------------------------------------------------------------------
configure_realm() {
  local token=$1
  local realm=$2

  echo "Configuring realm: $realm"

  curl -v -X PUT "$KEYCLOAK_URL/admin/realms/$realm" \
    -H "Authorization: Bearer $token" \
    -H "Content-Type: application/json" \
    -d "{
      \"realm\": \"$realm\",
      \"enabled\": true,
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
      \"bruteForceProtected\": true
    }" \
    && echo "Realm configured." || echo "Realm configuration failed."
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

  curl -sf -X POST "$KEYCLOAK_URL/admin/realms/$realm/clients" \
    -H "Authorization: Bearer $token" \
    -H "Content-Type: application/json" \
    -d "{
      \"clientId\": \"$client_id\",
      \"enabled\": true,
      \"clientAuthenticatorType\": \"client-secret\",
      \"secret\": \"$client_secret\",
      \"serviceAccountsEnabled\": true,
      \"standardFlowEnabled\": false,
      \"directAccessGrantsEnabled\": false,
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
  client_uuid=$(curl -sf "$KEYCLOAK_URL/admin/realms/$realm/clients?clientId=$client_id" \
    -H "Authorization: Bearer $token" \
    | grep -o '"id":"[^"]*' | head -1 | cut -d'"' -f4)

  local sa_user_id
  sa_user_id=$(curl -sf "$KEYCLOAK_URL/admin/realms/$realm/clients/$client_uuid/service-account-user" \
    -H "Authorization: Bearer $token" \
    | grep -o '"id":"[^"]*' | head -1 | cut -d'"' -f4)

  local realm_mgmt_id
  realm_mgmt_id=$(curl -sf "$KEYCLOAK_URL/admin/realms/$realm/clients?clientId=realm-management" \
    -H "Authorization: Bearer $token" \
    | grep -o '"id":"[^"]*' | head -1 | cut -d'"' -f4)

  for role in manage-users view-users query-users; do
    local role_json

    role_json=$(curl -sf "$KEYCLOAK_URL/admin/realms/$realm/clients/$realm_mgmt_id/roles/$role" \
      -H "Authorization: Bearer $token")

    curl -sf -X POST "$KEYCLOAK_URL/admin/realms/$realm/users/$sa_user_id/role-mappings/clients/$realm_mgmt_id" \
      -H "Authorization: Bearer $token" \
      -H "Content-Type: application/json" \
      -d "[$role_json]" \
      && echo "Role '$role' assigned." || echo "Role '$role' already assigned."
  done
}

# ------------------------------------------------------------------------------
# INIT FLOW
# ------------------------------------------------------------------------------
wait_for_keycloak

TOKEN=$(get_admin_token)
create_realm "$TOKEN" "$ACCOUNTS_KEYCLOAK_REALM"

TOKEN=$(get_admin_token)
configure_realm "$TOKEN" "$ACCOUNTS_KEYCLOAK_REALM"

TOKEN=$(get_admin_token)
create_client "$TOKEN" \
  "$ACCOUNTS_KEYCLOAK_REALM" \
  "$ACCOUNTS_KEYCLOAK_CLIENT_ID" \
  "$ACCOUNTS_KEYCLOAK_CLIENT_SECRET"

TOKEN=$(get_admin_token)
assign_service_account_roles \
  "$TOKEN" \
  "$ACCOUNTS_KEYCLOAK_REALM" \
  "$ACCOUNTS_KEYCLOAK_CLIENT_ID"

echo "***************************** [ END SCRIPT ] *****************************"