import 'package:http/http.dart' as http;
import 'dart:convert';

class NotificationService {
  static String? _webhookUrl;

  static Future<void> initialize() async {
    // Initialization logic if needed
  }

  static void startListening(String webhookUrl) {
    _webhookUrl = webhookUrl;
  }

  static void stopListening() {
    _webhookUrl = null;
  }

  static Future<void> sendNotification({
    required String packageName,
    required String title,
    required String text,
    required String? subText,
    String? tag,
    int? priority,
  }) async {
    if (_webhookUrl == null) return;

    try {
      final payload = {
        'packageName': packageName,
        'title': title,
        'text': text,
        'subText': subText,
        'tag': tag,
        'priority': priority,
        'timestamp': DateTime.now().toIso8601String(),
      };

      await http.post(
        Uri.parse(_webhookUrl!),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(payload),
      );
    } catch (e) {
      print('Error sending notification: $e');
    }
  }
}
