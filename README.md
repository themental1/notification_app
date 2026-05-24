# notification_app

Android notification listener app built with Flutter. Listens to system notifications and forwards them to a configurable HTTP webhook endpoint.

## Features

- 🔔 Real-time system notification monitoring
- 🌐 HTTP webhook integration
- 🎯 Package-specific filtering
- 🔐 Permission management
- 📊 Status dashboard

## Requirements

- Flutter 3.13+
- Android SDK 21+
- JDK 11+
- Gradle 7.0+

## Setup

1. Clone the repository:
```bash
git clone https://github.com/ornette-gr/notification_app.git
cd notification_app
```

2. Install dependencies:
```bash
flutter pub get
```

3. Configure Android:
```bash
flutter pub get
```

## Building

### Debug Build
```bash
flutter build apk --debug
```

### Release Build
```bash
flutter build apk --release
```

The APK will be available at `build/app/outputs/flutter-apk/app-release.apk`

## Usage

1. Install the APK on your Android device
2. Open the app
3. Enter your webhook URL (e.g., `http://10.0.10.11:8000/api/notifications`)
4. Grant notification listener permission in Android Settings
5. Tap "Start" to begin listening for notifications

### Webhook Payload Format

Each notification is sent as JSON POST request:

```json
{
  "packageName": "com.example.app",
  "title": "Notification Title",
  "text": "Notification content",
  "subText": "Additional info",
  "tag": "notification_tag",
  "priority": 2,
  "timestamp": "2024-01-15T10:30:45Z"
}
```

## Permissions Required

- `ACCESS_NOTIFICATION_POLICY` - Listen to notifications
- `BIND_NOTIFICATION_LISTENER_SERVICE` - Register as notification listener
- `INTERNET` - Send webhook requests
- `RECEIVE_BOOT_COMPLETED` - Auto-start on device boot

## Architecture

```
lib/
├── main.dart                    # App entry & UI
├── services/
│   └── notification_service.dart # Notification service bridge
└── models/
    └── notification_model.dart   # Data models

android/app/
├── build.gradle                 # App build config
└── src/main/
    ├── kotlin/
    │   ├── MainActivity.kt       # Flutter activity
    │   ├── NotificationListenerService.kt  # Listener service
    │   └── BootCompletedReceiver.kt        # Boot receiver
    └── AndroidManifest.xml      # Manifest & permissions
```

## CI/CD

GitHub Actions automatically builds and publishes releases:
- Builds on push to `main` and `develop` branches
- Builds on pull requests
- Publishes APK to GitHub Releases when tags are created

## License

MIT
