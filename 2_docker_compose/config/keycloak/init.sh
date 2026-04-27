#!/bin/bash
set -e
echo "***************************** [ INIT SCRIPT ] ****************************"

KEYCLOAK_URL="http://keycloak:8080"

# ------------------------------------------------------------ ( helpers init )
wait_for_keycloak() {
  echo "Waiting for Keycloak (auth ready)..."

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

get_admin_token() {
  curl -sf -X POST "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "grant_type=password" \
    -d "client_id=admin-cli" \
    -d "username=$KEYCLOAK_ADMIN_USER" \
    -d "password=$KEYCLOAK_ADMIN_PASSWORD" \
    | grep -o '"access_token":"[^"]*' | cut -d'"' -f4
}

create_realm() {
  local token=$1 realm=$2
  echo "Creating realm: $realm"
  curl -sf -X POST "$KEYCLOAK_URL/admin/realms" \
    -H "Authorization: Bearer $token" \
    -H "Content-Type: application/json" \
    -d "{\"realm\": \"$realm\", \"enabled\": true}" \
    && echo "Realm created." || echo "Realm already exists, skipping."
}

create_client() {
  local token=$1 realm=$2 client_id=$3 client_secret=$4
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
      \"publicClient\": false
    }" \
    && echo "Client created." || echo "Client already exists, skipping."
}

assign_service_account_roles() {
  local token=$1 realm=$2 client_id=$3

  # Get client internal ID
  local client_uuid
  client_uuid=$(curl -sf "$KEYCLOAK_URL/admin/realms/$realm/clients?clientId=$client_id" \
    -H "Authorization: Bearer $token" \
    | grep -o '"id":"[^"]*' | head -1 | cut -d'"' -f4)

  # Get service account user ID
  local sa_user_id
  sa_user_id=$(curl -sf "$KEYCLOAK_URL/admin/realms/$realm/clients/$client_uuid/service-account-user" \
    -H "Authorization: Bearer $token" \
    | grep -o '"id":"[^"]*' | head -1 | cut -d'"' -f4)

  # Get realm-management client ID
  local realm_mgmt_id
  realm_mgmt_id=$(curl -sf "$KEYCLOAK_URL/admin/realms/$realm/clients?clientId=realm-management" \
    -H "Authorization: Bearer $token" \
    | grep -o '"id":"[^"]*' | head -1 | cut -d'"' -f4)

  echo "Assigning roles to service account..."

  # Get each required role and assign
  for role in manage-users view-users query-users; do
    local role_json
    role_json=$(curl -sf "$KEYCLOAK_URL/admin/realms/$realm/clients/$realm_mgmt_id/roles/$role" \
      -H "Authorization: Bearer $token")

    curl -sf -X POST "$KEYCLOAK_URL/admin/realms/$realm/users/$sa_user_id/role-mappings/clients/$realm_mgmt_id" \
      -H "Authorization: Bearer $token" \
      -H "Content-Type: application/json" \
      -d "[$role_json]" \
      && echo "Role '$role' assigned." || echo "Role '$role' already assigned, skipping."
  done
}
# ------------------------------------------------------------- ( helpers end )

# ------------------------------------------------------------ ( keycloak init )
wait_for_keycloak
TOKEN=$(get_admin_token)
create_realm  "$TOKEN" "$ACCOUNTS_KEYCLOAK_REALM"
TOKEN=$(get_admin_token)
create_client "$TOKEN" "$ACCOUNTS_KEYCLOAK_REALM" "$ACCOUNTS_KEYCLOAK_CLIENT_ID" "$ACCOUNTS_KEYCLOAK_CLIENT_SECRET"
assign_service_account_roles "$TOKEN" "$ACCOUNTS_KEYCLOAK_REALM" "$ACCOUNTS_KEYCLOAK_CLIENT_ID"
# ------------------------------------------------------------- ( keycloak end )

echo "***************************** [ END SCRIPT ] *****************************"