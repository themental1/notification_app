# Release Keystore Setup for notification_app

This document explains how to generate the release keystore and configure GitHub Secrets.

## Step 1: Generate Release Keystore Locally

Generate a permanent keystore that will be used for all release builds:

```bash
keytool -genkey -v \
  -keystore release.keystore \
  -alias release_key \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass "NotificationApp2026!" \
  -keypass "NotificationApp2026!" \
  -dname "CN=Notification App,O=Notification App,L=Amaliada,ST=Elis,C=GR"
```

## Step 2: Encode Keystore as Base64

Convert the keystore to base64 for GitHub Secret storage:

```bash
cat release.keystore | base64 -w 0 > release.keystore.base64
cat release.keystore.base64
```

## Step 3: Add GitHub Secrets

Go to your GitHub repository:
1. Settings → Secrets and variables → Actions
2. Add the following secrets:

| Secret Name | Value |
|---|---|
| `RELEASE_KEYSTORE_BASE64` | (base64-encoded keystore from Step 2) |
| `KEYSTORE_STOREPASS` | `NotificationApp2026!` |
| `KEYSTORE_KEYPASS` | `NotificationApp2026!` |
| `KEYSTORE_ALIAS` | `release_key` |

## Step 4: Verify Setup

Once secrets are added, the workflow will:
- Automatically build release APKs when tags are pushed
- Sign with the permanent release keystore
- Keep the same certificate across all releases (no uninstall needed for updates)

### Build a Release:

```bash
git tag v1.0.0
git push origin v1.0.0
```

This will trigger the workflow to:
1. Build the release APK
2. Sign it with the permanent keystore
3. Create a GitHub Release with the signed APK

## Security Notes

- **Never commit `release.keystore` to git** (it's in .gitignore)
- Store the keystore file securely (backup to encrypted storage)
- Keep the passwords safe
- The GitHub secrets are encrypted and secure

## Local Testing (Optional)

To test locally with the release keystore:

```bash
export RELEASE_KEYSTORE_PATH=$(pwd)/release.keystore
export KEYSTORE_STOREPASS="NotificationApp2026!"
export KEYSTORE_KEYPASS="NotificationApp2026!"
export KEYSTORE_ALIAS="release_key"

flutter build apk --release
```

The signed APK will be at: `build/app/outputs/apk/release/app-release.apk`
