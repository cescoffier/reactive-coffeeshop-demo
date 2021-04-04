#!/usr/bin/env sh

# create secret
vault kv put secret/creds/barista billing-code=s3cr3t

# create encryption key
vault secrets enable transit
vault write -f transit/keys/my-encryption-key

# create users
vault auth enable userpass
vault write auth/userpass/users/coffeeshop password=c0ff33 policies=coffeeshop-policy
vault write auth/userpass/users/barista password=b0r1st0 policies=barista-policy

# create policies

cat <<EOF | vault policy write coffeeshop-policy -
path "transit/encrypt/my-encryption-key" {
  capabilities = [ "update" ]
}
EOF

cat <<EOF | vault policy write barista-policy -
path "secret/data/creds/barista" {
  capabilities = ["read"]
}
path "transit/decrypt/my-encryption-key" {
  capabilities = [ "update" ]
}
EOF