#!/bin/bash
set -e

# Generate release keystore
java -version
keytool -genkey -v \
  -keystore release.keystore \
  -alias release_key \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass "NotificationApp2026!" \
  -keypass "NotificationApp2026!" \
  -dname "CN=Notification App,O=Notification App,L=Amaliada,ST=Elis,C=GR"

# Encode as base64 for GitHub secret
echo "=== Keystore generated. Base64 encoding for GitHub Secret ==="
cat release.keystore | base64 -w 0 > release.keystore.base64
echo ""
echo "RELEASE_KEYSTORE_BASE64 secret value:"
cat release.keystore.base64
echo ""
echo ""
echo "Also save these values as GitHub Secrets:"
echo "KEYSTORE_STOREPASS=NotificationApp2026!"
echo "KEYSTORE_KEYPASS=NotificationApp2026!"
echo "KEYSTORE_ALIAS=release_key"
