#!/bin/bash

export VAULT_ADDR=https://vault.adeo.no

# Ensure user is authenicated, and run login if not.
gcloud auth print-identity-token &> /dev/null
if [ $? -gt 0 ]; then
    gcloud auth login
fi
kubectl config use-context dev-fss
kubectl config set-context --current --namespace=okonomi

# Login to Vault to fetch credentials
#[[ "$(vault token lookup -format=json | jq '.data.display_name' -r; exit ${PIPESTATUS[0]})" =~ "nav.no" ]] &>/dev/null || vault login -method=oidc -no-print

# Get database username and password from secret
DATABASE_USERNAME=$(kubectl -n okonomi get secret oppdrag-credentials -o jsonpath="{ .data['DATABASE_USERNAME'] }" | base64 -d)
DATABASE_PASSWORD=$(kubectl -n okonomi get secret oppdrag-credentials -o jsonpath="{ .data['DATABASE_PASSWORD'] }" | base64 -d)

# Get AZURE and DATABASE system variables
envValue=$(kubectl exec -it $(kubectl get pods | grep sokos-nav-oppdrag | cut -f1 -d' ') -c sokos-nav-oppdrag -- env | egrep "^AZURE")

# Set AZURE as local environment variables
rm -f defaults.properties
echo "$envValue" > defaults.properties
echo "DATABASE_USERNAME=$DATABASE_USERNAME" >> defaults.properties
echo "DATABASE_PASSWORD=$DATABASE_PASSWORD" >> defaults.properties
echo "AZURE env variables stores as defaults.properties"